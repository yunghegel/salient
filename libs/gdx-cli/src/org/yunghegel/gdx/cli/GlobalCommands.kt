package org.yunghegel.gdx.cli

import org.yunghegel.gdx.cli.arg.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

class GlobalCommands(val context : CLIContext) {

    @Command(name = "help", description = "print help")
    fun help() {
        with(context) {
            for ((namespace, commands) in commands) {
                StdOut.writeLn("Namespace: $namespace")
                val info = HashMap<String, Pair<String, String>>()
                for ((name, command) in commands) {
                    val func = command.function
                    val args = func.parameters.drop(1).map { param ->
                        when {
                            param.hasAnnotation<Argument>() -> {
                                val parameterAnnotation = param.findAnnotation<Argument>()!!
                                info[name] = Pair(parameterAnnotation.name, parameterAnnotation.description)
                                val name = parameterAnnotation.name
                                val type = param.type.classifier.toString().substringAfterLast(".").toLowerCase()

                                "$name: $type"
                            }

                            param.hasAnnotation<Option>() -> {
                                val optionAnnotation = param.findAnnotation<Option>()!!
                                info[name] = Pair(optionAnnotation.name, optionAnnotation.description)
                                val name = optionAnnotation.name
                                val type = param.type.classifier.toString().substringAfterLast(".").toLowerCase()
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
                    StdOut.writeLn("$name (${args.joinToString(", ")})")
                    info[name]?.takeIf { it.second.isNotEmpty() }?.let { StdOut.writeLn("\t${it.first} - ${it.second}") }
                }
                StdOut.writeLn("--------------------")
            }
        }
    }

    @Cmd("echo","echo message")
    fun echo(@Arg("message") message: String) {
        StdOut.writeLn(message)
    }

    @Cmd("set","set environment variable")
    fun set(@Arg("key") key: String, @Arg("value") value: String, @Flag("save") save: Boolean = false) {
        context.env[key] = value
        if (save) {
            saveEnv("env.txt")
        }
    }

    @Cmd("env","print environment variables")
    fun printenv() {
        for ((key, value) in context.env) {
            StdOut.writeLn("$key=$value")
        }
    }

    @Cmd("loadenv","load environment from file")
    fun loadEnv(@Arg("file") file: String) {
        val lines = java.io.File(file).readLines()
        for (line in lines) {
            val (key, value) = line.split("=", limit = 2)
            context.env[key] = value
        }
    }

    @Cmd("saveenv","save environment to file")
    fun saveEnv(@Arg("file", "filepath to save to") file: String) {
        java.io.File(file).writeText(context.env.map { (key, value) -> "$key=$value" }.joinToString("\n"))
    }

}