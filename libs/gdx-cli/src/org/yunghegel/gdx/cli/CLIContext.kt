package org.yunghegel.gdx.cli

import org.yunghegel.gdx.cli.arg.*
import org.yunghegel.gdx.cli.cmd.CLICommand
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

class CLIContext {

    internal val commands = mutableMapOf<String, MutableMap<String, CLICommand>>()

    internal val env = mutableMapOf<String, String>()

    fun registerCommands(obj: Any) {

        val kClass = obj::class
        val classNamespace = kClass.findAnnotation<Namespace>()?.name

        for (func in kClass.declaredFunctions) {
            val commandAnnotation = func.findAnnotation<Command>()

            val functionNamespace = func.findAnnotation<Namespace>()?.name
            val namespace = functionNamespace ?: classNamespace ?: "global"

            if (commandAnnotation != null) {
                commands.computeIfAbsent(namespace) { mutableMapOf() }[commandAnnotation.name] =
                    CLICommand(obj, func, commandAnnotation.description)
            }
        }
    }

    fun commandHelp(commandName: String) {
        for ((namespace, commands) in commands) {
            for ((name, command) in commands) {
                if (name == commandName) {
                    val map = HashMap<String, String>()
                    val func = command.function
                    val args = func.parameters.drop(1).map { param ->
                        when {
                            param.hasAnnotation<Argument>() -> {
                                val parameterAnnotation = param.findAnnotation<Argument>()!!
                                val name = parameterAnnotation.name
                                val type = param.type.classifier.toString().substringAfterLast(".").toLowerCase()
                                map[name] = parameterAnnotation.description
                                "$name: ${type.blue()}"
                            }

                            param.hasAnnotation<Option>() -> {
                                val optionAnnotation = param.findAnnotation<Option>()!!
                                val name = optionAnnotation.name
                                val type = param.type.classifier.toString().substringAfterLast(".").toLowerCase()
                                map[name] = optionAnnotation.description
                                "$name: ${type.blue()}"
                            }

                            param.hasAnnotation<Flag>() -> {
                                val flagAnnotation = param.findAnnotation<Flag>()!!
                                val name = flagAnnotation.name
                                map[name] = flagAnnotation.description
                                "[${name.cyan()}]"
                            }

                            else -> throw IllegalArgumentException("Unknown parameter type: ${param.name}")
                        }
                    }
                    println("${name.green()} (${args.joinToString(", ")}) - ${command.description}")
                    map.forEach { (key, value) ->
                        if (value.isNotEmpty()) {
                            println("\t${key.cyan()} - ${value}")
                        }
                    }
                    return
                }
            }
        }
    }

    fun namespaceHelp(namespace: String) {
        for ((ns, commands) in commands) {
            if (ns == namespace) {
                for ((name, command) in commands) {
                    commandHelp(name)
                }
            }
        }
    }


}