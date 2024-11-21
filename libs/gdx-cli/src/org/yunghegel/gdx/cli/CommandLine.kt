package org.yunghegel.gdx.cli

import org.yunghegel.gdx.cli.arg.Argument
import org.yunghegel.gdx.cli.arg.Flag
import org.yunghegel.gdx.cli.arg.Option
import org.yunghegel.gdx.cli.arg.Parser
import org.yunghegel.gdx.cli.cmd.CommandExecutor
import org.yunghegel.gdx.cli.input.CommandHistory
import org.yunghegel.gdx.cli.input.CommandLineInput
import org.yunghegel.gdx.cli.input.ParsedCommandInput
import org.yunghegel.gdx.cli.input.ParsedValueInput
import org.yunghegel.gdx.cli.util.StdOut
import org.yunghegel.gdx.cli.util.ValueAction
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

open class CommandLine() : CommandExecutor,CommandLineInput {

    val history : CommandHistory = CommandHistory()

    val context : CLIContext = CLIContext()

    val parser : Parser = Parser(context)

    open var writeLine : (String) -> Unit = { println(it) }

    init {
        context.register(GlobalCommands(context))


    }

    context(ParsedCommandInput)
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

    context(ParsedValueInput)
    fun executeValue() {

        arguments.forEach { (key, value) ->
            when(action) {
                ValueAction.SET -> {
                    val cliVal = cliVal
                    val accessor = cliVal.accessor
                    val castedValue = parser.convertValue(value, accessor.type)
                    accessor.set(castedValue)
                }
                ValueAction.PRINT -> {
                    val cliVal = cliVal
                    val accessor = cliVal.accessor
                    StdOut.writeLn("${accessor.name}: ${accessor.get()}")
                }
                ValueAction.HELP -> {
                    val cliVal = cliVal
                    StdOut.writeLn("${cliVal.accessor.name}: ${cliVal.value.type}\n \r - ${cliVal.value.description}")
                }
            }
        }
    }

    fun sanitizeInput(string: String) : String {
        return string.trim().replace("\\s+".toRegex(), " ")
    }

    override fun acceptInput(string: String) {

        val string = sanitizeInput(string)

        history.addCommand(string)
        var args = string.split(" ").toTypedArray()

        if (args.isEmpty()) { StdOut.writeErr("Empty input"); return }

        if (args[0] !in context.namespaces) {
            if (context.namespace != "global") {
                args = arrayOf(context.namespace) + args
            } else {
                args = arrayOf("global") + args
            }
        }



        val parsedInput = parser.parse(context,args)

        parsedInput?.let { input ->
            when(input) {
                is ParsedCommandInput -> {
                    with(input) {
                        val result = executeCommand()
                        result?.let { writeLine(result.toString()) }
                    }
                }
                is ParsedValueInput -> {
                    with(input) {
                        executeValue()
                    }
                }
            }
        }
    }

    fun register(vararg objects : Any) {
        for (obj in objects) {
            context.register(obj)
        }
    }

}