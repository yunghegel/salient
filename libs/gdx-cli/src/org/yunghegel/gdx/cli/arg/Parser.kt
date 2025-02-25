package org.yunghegel.gdx.cli.arg

import org.yunghegel.gdx.cli.*
import org.yunghegel.gdx.cli.cmd.CLICommand
import org.yunghegel.gdx.cli.input.ParsedCommandInput
import org.yunghegel.gdx.cli.input.ParsedInput
import org.yunghegel.gdx.cli.input.ParsedValueInput
import org.yunghegel.gdx.cli.util.ValueAction
import org.yunghegel.gdx.cli.value.CLIValue
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

class Parser(val context : CLIContext) {






    fun joinQuotedArgs(args: Array<String>): Array<String> {
        val joinedArgs = mutableListOf<String>()
        var quoted = false
        var single = false
        var currentArg = ""
        for (arg in args) {
            if (arg.startsWith("\"") || arg.startsWith("'")) {
                quoted = true
                single = arg.startsWith("'")
                currentArg = arg.substring(1)
            } else if (arg.endsWith("\"") || arg.endsWith("'")) {
                quoted = false
                currentArg += if (single)" $arg".substringBeforeLast("'")  else " $arg".substringBeforeLast("\"")
                joinedArgs.add(currentArg)
            } else if (quoted) {
                currentArg += " $arg"
            } else {
                joinedArgs.add(arg)
            }
        }
        return joinedArgs.toTypedArray()
    }

    fun replaceEnvVars(args: Array<String>): Array<String> {
        return args.map { arg ->
            if (arg.startsWith("$")) {
                context.env[arg.substring(1)] ?: arg
            } else {
                arg
            }
        }.toTypedArray()
    }





    fun findValue(name: String): CLIValue {
        val parts = name.split(".")
        if (parts.size == 1) {
            return context.values["global"]?.get(name) ?: throw IllegalArgumentException("Unknown value: $name")
        } else {
            val ns = parts[0]
            val name = parts[1]
            return context.values[ns]?.get(name) ?: throw IllegalArgumentException("Unknown value: $ns $name")
        }
    }

    fun isValueCommand(args : Array<String>) : Boolean {
        if (ValueAction.all.contains(args[0])) {
            return true
        }
        return false
    }

    fun parse(context:CLIContext, args: Array<String>) : ParsedInput? {
        val arguments = mutableMapOf<String, String>()
        val options = mutableMapOf<String, String>()
        val flags = mutableSetOf<String>()

        println(args.contentToString())

        val cmd = context.run {
            if (args.isEmpty()) {
                throw IllegalArgumentException("No command provided")
            }
            var args = joinQuotedArgs(args)

            args.forEachIndexed { index, arg ->
                if (arg.startsWith("$")) {
                    args[index] = env[arg.substring(1)] ?: arg
                }
            }

            if (args.contains("&&")) {
                val index = args.indexOf("&&")
                val first = args.sliceArray(0 until index)
                val second = args.sliceArray(index + 1 until args.size)
                parse(context,first)
                parse(context,second)
                return null
            }

            if (isValueCommand(args)) {

                val action : ValueAction = ValueAction.fromString(args[0]) ?: throw IllegalArgumentException("Unknown action: ${args[0]}")

                val ns : String
                val name : String

                if (args[1].contains(".")) {
                    ns = args[1].substringBefore(".")
                    name = args[1].substringAfter(".")
                } else if(context.namespace != "global") {
                    ns = context.namespace
                    name = args[1]

                }else {
                    ns = "global"
                    name = args[1]
                }

                val target = "$ns.$name"

                val cliValue = this@Parser.findValue(target)

                when(action) {
                    ValueAction.SET -> {
                        println(args.joinToString())

                        val inputValue = args[2]
                        val input = if (inputValue.startsWith("$")) {
                            env[inputValue.substring(1)] ?: inputValue
                        } else {
                            inputValue
                        }
                        return ParsedValueInput(cliValue, ValueAction.SET, mutableMapOf(args[0] to input))

                    }
                    ValueAction.PRINT -> {
                        return ParsedValueInput(cliValue, ValueAction.PRINT, mutableMapOf(args[0]to ""))
                    }
                    ValueAction.HELP -> {
                        return ParsedValueInput(cliValue, ValueAction.HELP, mutableMapOf("help" to ""))
                    }
                }
            }

            val ns = if (context.namespace != "global") arrayOf("global",context.namespace, ) else args[0]

//        if not namespaced, insert global namespace
            if (args[0] !in commands.keys) {
                args = arrayOf("global") + args
            }

            val namespace: String = args[0]



            val commandName: String = args[1]

            if (args.contains("--help") || args.contains("-h")) {
                if (args.size == 2) {
                    context.commands.namespace(namespace)
                } else {
                    context.commands.help(commandName)
                }
                return null
            }

            val command = commands[namespace]?.get(commandName) ?: commands[context.namespace]?.get(commandName) ?: commands["global"]?.get(commandName)
            ?: throw IllegalArgumentException("Unknown command: $namespace $commandName")

            for (arg in args.drop(2)) {
                when {
                    arg.startsWith("--") -> {
                        val parts = arg.substring(2).split("=")
                        if (parts.size == 2) {
                            options[parts[0]] = parts[1]
                        } else {
                            flags.add(parts[0])
                        }
                    }

                    arg.startsWith("-") -> {
                        flags.add(arg.substring(1))
                    }

                    arg.contains("=") -> {
                        val parts = arg.split("=")
                        if (parts.size == 2) {
                            arguments[parts[0]] = parts[1]
                        }
                    }

                    else -> {
                        if (arguments.size < command.function.parameters.size - 1) {
                            arguments[command.function.parameters[arguments.size + 1].name!!] = arg
                        }
                    }
                }
            }
            command
        }
        return ParsedCommandInput(cmd, arguments, options, flags)
    }

    private fun invokeCommand(
        command: CLICommand,
        arguments: Map<String, String>,
        options: Map<String, String>,
        flags: Set<String>
    ) : Any? {
        val (obj, func) = command
        val params = func.parameters.drop(1).map { param ->
            when {
                param.hasAnnotation<Argument>() -> {
                    val name = param.findAnnotation<Argument>()!!.name
                    val value = arguments[name] ?: throw IllegalArgumentException("Missing argument: $name")
                    convertValue(value, param.type.classifier as KClass<*>)
                }

                param.hasAnnotation<Option>() -> {
                    val annotation = param.findAnnotation<Option>()!!
                    val name = annotation.name
                    val shortcut = annotation.name[0].toString()
                    val value = options[name] ?: options[shortcut] ?: annotation.default
                    convertValue(value, param.type.classifier as KClass<*>)
                }

                param.hasAnnotation<Flag>() -> {
                    val annotation = param.findAnnotation<Flag>()!!
                    val name = annotation.name
                    val shortcut = annotation.name[0].toString()
                    flags.contains(annotation.name) || flags.contains(annotation.key) || flags.contains(shortcut)
                }

                else -> throw IllegalArgumentException("Unknown parameter type: ${param.name}")
            }
        }
        return func.call(obj, *params.toTypedArray())
    }



    fun convertValue(value: String, targetType: KClass<*>): Any {
        return when (targetType) {
            String::class -> value
            Int::class -> value.toInt()
            Float::class -> value.toFloat()
            Boolean::class -> value.toBoolean()
            List::class -> {
                parseList(value, inferListTypeFromValue(value))
            }

            else -> throw IllegalArgumentException("Unsupported argument type: $targetType")
        }
    }

    fun inferListTypeFromValue(value: String): KClass<*> {
        val values = value.trim('(', ')').split(',').map { it.trim() }
        return when {
            values.all { it.toIntOrNull() != null } -> Int::class
            values.all { it.toFloatOrNull() != null } -> Float::class
            values.all { it.toBoolean() != null } -> Boolean::class
            else -> String::class
        }
    }

    fun parseList(value: String, type: KClass<*>): List<*> {
        val values = value.trim('(', ')').split(',').map { it.trim() }
        return when (type) {
            Int::class -> values.map { it.toInt() }
            Float::class -> values.map { it.toFloat() }
            Boolean::class -> values.map { it.toBoolean() }
            else -> values
        }
    }

    @Command(name = "help", description = "print help")
    fun help() {
        for ((namespace, commands) in context.commands) {
            println("Namespace: $namespace")
            val info = HashMap<String, Pair<String, String>>()
            for ((name, command) in commands) {
                val func = command.function
                val args = func.parameters.drop(1).map { param ->
                    when {
                        param.hasAnnotation<Argument>() -> {
                            val parameterAnnotation = param.findAnnotation<Argument>()!!
                            info[name] = Pair(parameterAnnotation.name, parameterAnnotation.description)
                            val name = parameterAnnotation.name
                            val type = param.type.classifier.toString().substringAfterLast(".").lowercase()

                            "$name: $type"
                        }

                        param.hasAnnotation<Option>() -> {
                            val optionAnnotation = param.findAnnotation<Option>()!!
                            info[name] = Pair(optionAnnotation.name, optionAnnotation.description)
                            val name = optionAnnotation.name
                            val type = param.type.classifier.toString().substringAfterLast(".").lowercase()
                            "$name: $type"
                        }

                        param.hasAnnotation<Flag>() -> {
                            val flagAnnotation = param.findAnnotation<Flag>()!!
                            info[name] = Pair(flagAnnotation.name, flagAnnotation.description)
                            val name = flagAnnotation.name
                            "[$name]"
                        }

                        else -> throw IllegalArgumentException("Unknown parameter type: ${param.name}")
                    }
                }
                println("$name (${args.joinToString(", ")})")
                info[name]?.takeIf { it.second.isNotEmpty() }?.let { println("\t${it.first} - ${it.second}") }
            }
            println("--------------------")
        }
    }

    @Cmd("echo","echo message")
    fun echo(@Arg("message") message: String) {
        println(message)
    }


}