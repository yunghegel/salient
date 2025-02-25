package org.yunghegel.gdx.cli

import org.yunghegel.gdx.cli.arg.*
import org.yunghegel.gdx.cli.cmd.CLICommand
import org.yunghegel.gdx.cli.util.blue
import org.yunghegel.gdx.cli.util.cyan
import org.yunghegel.gdx.cli.util.green
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

@Namespace("global")
class Commands : HashMap<String, MutableMap<String, CLICommand>>() {

    @Command("help", "print help")
    fun help(@Arg("name","The name of the command to get help about.") commandName: String) {
        for ((namespace, commands) in this) {
            for ((name, command) in commands) {
                if (name == commandName) {
                    val map = HashMap<String, String>()
                    val func = command.function
                    val args = func.parameters.drop(1).map { param ->
                        when {
                            param.hasAnnotation<Argument>() -> {
                                val parameterAnnotation = param.findAnnotation<Argument>()!!
                                val name = parameterAnnotation.name
                                val type = param.type.classifier.toString().substringAfterLast(".").lowercase()
                                map[name] = parameterAnnotation.description
                                "$name: ${type.blue()}"
                            }

                            param.hasAnnotation<Option>() -> {
                                val optionAnnotation = param.findAnnotation<Option>()!!
                                val name = optionAnnotation.name
                                val type = param.type.classifier.toString().substringAfterLast(".").lowercase()
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

    fun namespace(namespace: String) {
        for ((ns, commands) in this) {
            if (ns == namespace) {
                for ((name, command) in commands) {
                    help(name)
                }
            }
        }
    }

}