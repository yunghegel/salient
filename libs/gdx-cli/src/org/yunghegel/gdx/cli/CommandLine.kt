package org.yunghegel.gdx.cli

import org.yunghegel.gdx.cli.arg.*
import org.yunghegel.gdx.cli.cmd.CLICommand
import org.yunghegel.gdx.cli.cmd.CommandExecutor
import org.yunghegel.gdx.cli.input.CommandHistory
import org.yunghegel.gdx.cli.input.CommandLineInput
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

open class CommandLine() : CommandExecutor,CommandLineInput {

    val history : CommandHistory = CommandHistory()

    val context : CLIContext = CLIContext()

    val parser : Parser = Parser()

    open var writeLine : (String) -> Unit = { println(it) }

    init {
        context.registerCommands(GlobalCommands(context))
    }

    context(ParsedInput)
    override fun executeCommand() : Any? {
        val (obj, func) = command
        val params = func.parameters.drop(1).map { param ->
            when {
                param.hasAnnotation<Argument>() -> {
                    val name = param.findAnnotation<Argument>()!!.name
                    val value = arguments[name] ?: throw IllegalArgumentException("Missing argument: $name")
                    parser.convertValue(value, param.type.classifier as KClass<*>)
                }

                param.hasAnnotation<Option>() -> {
                    val annotation = param.findAnnotation<Option>()!!
                    val name = annotation.name
                    val shortcut = annotation.name[0].toString()
                    val value = options[name] ?: options[shortcut] ?: annotation.default
                    parser.convertValue(value, param.type.classifier as KClass<*>)
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

    override fun acceptInput(string: String) {
        history.addCommand(string)
        val args = string.split(" ").toTypedArray()
        val parsedInput = parser.parse(context,args)
        parsedInput?.let { input ->
            with(input) {
                executeCommand()
            }
        }
    }

    fun register(any : Any) {
        context.registerCommands(any)
    }

}