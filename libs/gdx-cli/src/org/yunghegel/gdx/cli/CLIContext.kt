package org.yunghegel.gdx.cli

import org.yunghegel.gdx.cli.arg.Command
import org.yunghegel.gdx.cli.arg.Namespace
import org.yunghegel.gdx.cli.arg.Value
import org.yunghegel.gdx.cli.cmd.CLICommand
import org.yunghegel.gdx.cli.util.Type
import org.yunghegel.gdx.cli.value.CLIValue
import org.yunghegel.gdx.utils.reflection.KAccessor
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaField

class CLIContext {

    val commands: Commands = Commands()

    val env: CLIEnvironment = CLIEnvironment()

    val values: Values = Values()

    var namespace: String = "global"
        set(value) {
            nsChanged(field,value)
            field = value

        }

    val namespaces : List<String>
        get() = (commands.keys.toList() + values.keys.toList()).distinct()

    var nsChanged : (String,String) -> Unit = {_,_ ->}

    init {

        register(commands, values, env)

    }

    fun findCommand(name :String) : Pair<String,CLICommand>? {
        for ((namespace, commands) in commands) {
            for ((commandName, command) in commands) {
                if (commandName == name) {
                    return Pair(namespace,command)
                }
            }
        }
        return null
    }

    fun findValue(name :String) : Pair<String,CLIValue>? {
        for ((namespace, values) in values) {
            for ((valueName, value) in values) {
                if (valueName == name) {
                    return Pair(namespace,value)
                }
            }
        }
        return null
    }

    private fun scan(obj: Any) {

        val kClass = obj::class
        val classNamespace = kClass.findAnnotation<Namespace>()?.name

        scanFields(obj, classNamespace ?: "global")
        scanCommands(obj, classNamespace ?: "global")

    }

    fun scanFields(obj: Any, namespace: String = "global") {
        val values : MutableList<CLIValue> = mutableListOf()
        val props = obj::class.members
        for (prop in props) {
            if (prop is KMutableProperty1<*,*>) {
                val valueAnnotation = prop.annotations.find { it is Value } as Value?
                if (valueAnnotation != null) {
                    val annotation = valueAnnotation
                    val value = prop.getter.call(obj)
                    val accessor: KAccessor = object : KAccessor {
                        override fun get(): Any {
                            return prop.getter.call(obj) as Any
                        }

                        override fun set(value: Any) {
                            try {
                                val castedValue = annotation.type.parse(value.toString())
                                if (annotation.type.matches(castedValue)) {
                                    prop.setter.call(obj, castedValue)
                                    println("Set ${prop.name} to $value")
                                } else {
                                    println("Value $value does not match type ${annotation.type}")
                                }
                            } catch (e: Exception) {
                                println("Failed to set value $value to ${prop.name}")
                                e.printStackTrace()
                            }
                        }

                        override val name: String
                            get() = prop.name

                        override val type: KClass<*>
                            get() = prop.returnType.classifier as KClass<*>
                    }
                    val cliVal = CLIValue(accessor, annotation)
                    this.values.computeIfAbsent(namespace) { HashMap() }[cliVal.accessor.name] = cliVal

                } else {
                    val javaField = prop.javaField
                    if (javaField != null && javaField.type.isPrimitive) {
                        val isStatic = java.lang.reflect.Modifier.isStatic(javaField.modifiers)
                        if (isStatic) {
                            // Skip static fields like InjectionContext.loggingEnabled
                            continue
                        }

                        if (javaField.canAccess(obj)) {
                            Type.fromClass(javaField.type)?.let {
                                val accessor: KAccessor = object : KAccessor {
                                    override fun get(): Any {
                                        return prop.getter.call(obj) as Any
                                    }

                                    override fun set(value: Any) {
                                        prop.setter.call(obj, value)
                                    }

                                    override val name: String
                                        get() = prop.name

                                    override val type: KClass<*>
                                        get() = prop.returnType.classifier as KClass<*>
                                }
                                val cliVal = CLIValue(accessor, Value(it, ""))
                                this.values
                                    .computeIfAbsent(obj::class.simpleName!!.lowercase()) { HashMap() }[cliVal.accessor.name] = cliVal
                            }
                        }
                    }
                }



            }
        }

    }

    private fun scanCommands(obj: Any, namespace: String = "global") {
        val kClass = obj::class
        for (func in kClass.declaredFunctions) {
            val commandAnnotation = func.findAnnotation<Command>()
            if (commandAnnotation != null) {
                commands.computeIfAbsent(namespace) { mutableMapOf() }[commandAnnotation.name] =
                    CLICommand(obj, func, commandAnnotation.description)
            }
        }
    }

    fun register(vararg objects: Any) {
        for (obj in objects) {
            scan(obj)
        }
    }

}