package org.yunghegel.gdx.cli.input

import org.yunghegel.gdx.cli.arg.Argument
import org.yunghegel.gdx.cli.arg.ArgumentKind

/**
 * Resolves a list of arguments, of each kind (-f, --flag=value, --flag value, key=value, key value)
 * and associates them with a given argument kind (flag, option, argument) and input kind (single, map, list).
 */
object InputResolver {

    /**
     * Resolves a list of arguments, of each kind (-f, --flag=value, --flag value, key=value, key value)
     * and associates them with a given argument kind (flag, option, argument) and input kind (single, map, list).
     *
     * @param args The list of arguments to resolve, assuming the name of the command is seperated from the arguments provided.
     * @return A map of resolved arguments.
     */
    fun resolve(input:List<String>): List<Result> {
        val results = mutableListOf<Result>()
        println(input.joinToString(" "))

        var last = ""
        var previousspace =false
        for (arg in input) {
            if (!last.startsWith("-") && !last.startsWith("--") && last != "") {
                    val argument = "$last $arg"
                    val argKind = resolveArgumentKind(argument)
                    val format = resolveFormat(argument)
                    val valueKind = resolveValueKind(argument)
                    results.add(Result(argument, argKind, format, valueKind))
            } else {
                val argKind = resolveArgumentKind(arg)
                val format = resolveFormat(arg)
                val valueKind = resolveValueKind(arg)
                results.add(Result(arg, argKind, format, valueKind))
            }

            last = arg
        }

        return results
    }

    /**
     * when key value : "key value" , key=value : "key=value", key1=value1 key2=value2 : "key1=value1 key2=value2"
     */
    fun resolveArgumentString(format: InputFormat,argKind: ArgumentKind, input : String) : String {
        println("$format $argKind $input")
        return when (format) {
            InputFormat.KEY_VALUE -> {
                val (key, value) = input.split("=")
                "$key $value"
            }
            InputFormat.ORDERED -> {
                if (argKind==ArgumentKind.FLAG) {
                    input
                } else {
                    val (key, value) = input.split(" ")
                    "$key $value"
                }
            }
            InputFormat.COMMA_SEPARATED -> {
                input
            }
        }
    }

    fun parseLine(input: String): List<Argument> {
//        after the first index of space, the arguments are provided
        val argStart = input.indexOf(" ")
        val command = input.substring(0, argStart)
        val argString = input.substring(argStart + 1)

//        we need to split the arguments, but ordered arguments are split by space
//        we need to walk through the string and split by space, only when the previous character is not a key-value separator
        val arguments = mutableListOf<Argument>()
//        if a key is not prefixed by - or --, it is a positional argument
//        we know that it is then followed by a space or =, so we substring until the next space or = for the ky,
//        and the rest of the string is the value
        val results = resolve(argString.split(" "))
        for (result in results) {
            arguments.add(parseResult(result))
        }

        return arguments
    }

    fun parseResult(result: Result): Argument{
        var argument : Argument? = null
        val arg = result.arg
        val argKind = result.argKind
        val format = result.format

        println(result)

        if (argKind == ArgumentKind.FLAG) {
            assert(arg.startsWith("-") && !arg.startsWith("--"))
//            when -f, f
            val flag = arg.substring(1)
           argument = (Argument(flag, true, argKind))
        } else if (argKind == ArgumentKind.OPTION) {
            assert(arg.startsWith("--"))
            val opt = arg.substring(2)
            assert(result.format != InputFormat.COMMA_SEPARATED)
            if (result.format == InputFormat.KEY_VALUE) {
                val (key, value) = arg.split("=")
                argument = (Argument(key, value, argKind))
                println(argument)
            } else {
                val (key, value) = arg.split(" ")
                argument = (Argument(key, value, argKind))
                println(argument)
            }
        } else {
            assert(argKind == ArgumentKind.ARGUMENT)
            println(arg)
            when (result.valueKind) {
                ValueKind.SINGLE -> {
                    when(result.format) {

                        InputFormat.KEY_VALUE -> {
                            val (key, value) = arg.split("=")
                            argument = (Argument(key, value, argKind))
                        }
                        InputFormat.ORDERED -> {
                            if (arg.contains(" ")) {
                                val (key, value) = arg.split(" ")
                                argument = (Argument(key, value, argKind))
                            } else {
                                argument = (Argument(arg, arg, argKind))
                            }
                        }
                        InputFormat.COMMA_SEPARATED -> {
                            val list = arg.split(",")
                            argument = (Argument(arg, list, argKind))
                        }

                    }
                }
                ValueKind.LIST -> {
                    val list = arg.substring(1, arg.length - 1).split(",")
                    argument = (Argument(arg, list, argKind))
                }
                ValueKind.MAP -> {
                    val map = arg.substring(1, arg.length - 1).split(" ")
                    val mapArgs = mutableMapOf<String, String>()
                    for (pair in map) {
                        val (key, value) = pair.split("=")
                        mapArgs[key] = value
                    }
                    argument = (Argument(arg, mapArgs, argKind))
                }
            }
        }
        return argument
    }

    /*
     * A list is provided as (value1 value2 value3) or (value1, value2, value3)
     * A map is provided as [key1=value1 key2=value2 key3=value3] or [key1=value1, key2=value2, key3=value3]
     * A single input is provided as key val( key=value, key value ). Options and flags (-f, --flag=value, --flag value) are considered single inputs as well)
     */
    fun resolveValueKind(value: String): ValueKind {
        return when {
            value.startsWith("[") && value.endsWith("]") -> ValueKind.MAP
            value.startsWith("(") && value.endsWith(")") -> ValueKind.LIST
            else -> ValueKind.SINGLE
        }
    }

    fun resolveFormat(value: String): InputFormat {
        return when {
            value.contains("=") -> InputFormat.KEY_VALUE
            value.contains(",") -> InputFormat.COMMA_SEPARATED
            else -> InputFormat.ORDERED
        }
    }

    fun resolveArgumentKind(value: String): ArgumentKind {
        return when {
            value.startsWith("--") -> ArgumentKind.OPTION
            value.startsWith("-") -> ArgumentKind.FLAG
            else -> ArgumentKind.ARGUMENT
        }
    }

    data class Result(val arg: String,val argKind : ArgumentKind, val format: InputFormat, val valueKind: ValueKind)


}