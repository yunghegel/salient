package org.yunghegel.gdx.cli

import com.github.tommyettinger.textra.utils.Palette.blue
import org.yunghegel.gdx.cli.arg.*
import org.yunghegel.gdx.cli.util.StdOut
import org.yunghegel.gdx.cli.util.blue
import org.yunghegel.gdx.cli.util.green
import org.yunghegel.gdx.cli.util.purple
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

class GlobalCommands(val context : CLIContext) {

    @Command(name = "help", description = "print help")
    fun help() {
        with(context) {
            for ((namespace, commands) in commands) {
                StdOut.writeLn("| ${(namespace.blue())}")
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

                                "${name.purple()}: ${type.purple()}"
                            }

                            param.hasAnnotation<Option>() -> {
                                val optionAnnotation = param.findAnnotation<Option>()!!
                                info[name] = Pair(optionAnnotation.name, optionAnnotation.description)
                                val name = optionAnnotation.name
                                val type = param.type.classifier.toString().substringAfterLast(".").toLowerCase()
                                "${name.purple()}: ${type.purple()}"
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
                    StdOut.writeLn("${name.green()} (${args.joinToString(", ")})")
                    info[name]?.takeIf { it.second.isNotEmpty() }?.let { StdOut.writeLn("\t${it.first} - ${it.second}") }
                }
                StdOut.writeLn("\n")
            }
            for ((namespace, values) in values) {
                StdOut.writeLn("| ${(namespace.blue())}")
                for ((name, value) in values) {
                    StdOut.writeLn("$name: ${value.value.type}\n \r - ${value.value.description}")
                }
                StdOut.writeLn("\n")
            }
        }
    }

    @Cmd("echo","echo message")
    fun echo(@Arg("message") message: String) {
        StdOut.writeLn(message)
    }



    @Cmd("describe","describe namespace, command or value")
    fun describe(@Arg("name") name: String) {
        with(context) {
             context.findCommand(name)?.let  {
                 val (namespace, command) = it
                 if (command != null) {
                     val func = command.function
                     val args = func.parameters.drop(1).map { param ->
                         when {
                             param.hasAnnotation<Argument>() -> {
                                 val parameterAnnotation = param.findAnnotation<Argument>()!!
                                 val name = parameterAnnotation.name
                                 val type = param.type.classifier.toString().substringAfterLast(".").toLowerCase()
                                 "$name: $type"
                             }

                             param.hasAnnotation<Option>() -> {
                                 val optionAnnotation = param.findAnnotation<Option>()!!
                                 val name = optionAnnotation.name
                                 val type = param.type.classifier.toString().substringAfterLast(".").toLowerCase()
                                 "$name: $type"
                             }

                             param.hasAnnotation<Flag>() -> {
                                 val flagAnnotation = param.findAnnotation<Flag>()!!
                                 val name = flagAnnotation.name
                                 "[$name]"
                             }

                             else -> throw IllegalArgumentException("Unknown parameter type: ${param.name}")
                         }
                     }
                     StdOut.writeLn("$name (${args.joinToString(", ")})")
                 } else {
                     val cliValue = context.findValue(name)
                     if (cliValue != null){
                         val (namespace, value) = cliValue
                         StdOut.writeLn("$name: ${value.value.type}\n \r - ${value.value.description}")
                     } else {
                         if (namespaces.contains(name)) {
                            values[name]?.forEach { (valueName, value) ->
                                StdOut.writeLn("$valueName: ${value.value.type}\n \r - ${value.value.description}")
                            }
                             commands[name]?.forEach { (commandName, command) ->
                                 val func = command.function
                                 val args = func.parameters.drop(1).map { param ->
                                     when {
                                         param.hasAnnotation<Argument>() -> {
                                             val parameterAnnotation = param.findAnnotation<Argument>()!!
                                             val name = parameterAnnotation.name
                                             val type = param.type.classifier.toString().substringAfterLast(".").toLowerCase()
                                             "$name: $type"
                                         }

                                         param.hasAnnotation<Option>() -> {
                                             val optionAnnotation = param.findAnnotation<Option>()!!
                                             val name = optionAnnotation.name
                                             val type = param.type.classifier.toString().substringAfterLast(".").toLowerCase()
                                             "$name: $type"
                                         }

                                         param.hasAnnotation<Flag>() -> {
                                             val flagAnnotation = param.findAnnotation<Flag>()!!
                                             val name = flagAnnotation.name
                                             "[$name]"
                                         }

                                         else -> throw IllegalArgumentException("Unknown parameter type: ${param.name}")
                                     }
                                 }
                                 StdOut.writeLn("$commandName (${args.joinToString(", ")})")
                             }
                         } else {
                             StdOut.writeLn("Unknown command or value: $name")
                         }
                     }
                 }
             }
            }

        }

    @Cmd("use","clear console")
    fun use(@Arg("namespace","namespace to use") namespace: String) {
        if (namespace in context.namespaces) {
            context.namespace = namespace
            StdOut.writeLn("Using namespace: $namespace")
        } else {
            StdOut.writeErr("Unknown namespace: $namespace \n Available namespaces: ${context.namespaces.joinToString(", ")}")
        }
    }



}