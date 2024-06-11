data class ParsedArgument(
    val flags: Set<String> = emptySet(),
    val options: Map<String, Any?> = emptyMap(),
    val arguments: Map<String, Any?> = emptyMap()
)

fun parseArguments(input: String): ParsedArgument {
    val args = input.split(" ")
    val flags = mutableSetOf<String>()
    val options = mutableMapOf<String, Any?>()
    val arguments = mutableMapOf<String, Any?>()

    for (arg in args) {
        when {
            arg.startsWith("-") -> {
                flags.addAll(arg.substring(1).split("").filter { it.isNotEmpty() })
            }
            arg.startsWith("--") -> {
                val (key, value) = parseKeyValue(arg.substring(2), '=')
                options[key] = parseValue(value)
            }
            else -> {
                val (key, value) = parseKeyValue(arg, '=')
                arguments[key] = parseValue(value)
            }
        }
    }

    return ParsedArgument(flags, options, arguments)
}

fun parseKeyValue(arg: String, separator: Char): Pair<String, String?> {
    val parts = arg.split(separator, limit = 2)
    return Pair(parts[0], if (parts.size > 1) parts[1] else null)
}

fun parseValue(value: String?): Any? {
    if (value == null) return null

    if (value.startsWith("[") && value.endsWith("]")) {
        val mapValues = value.substring(1, value.length - 1).split(" ").map { parseKeyValue(it, '=') }
        return mapValues.associate { (key, value) -> key to parseValue(value) }
    } else if (value.startsWith("(") && value.endsWith(")")) {
        val listValues = value.substring(1, value.length - 1).split(",").map { it.trim() }
        return listValues.map { parseValue(it) }
    } else if (value.contains("'") || value.contains("\"")) {
        return value.removeSurrounding("'", "'").removeSurrounding("\"", "\"")
    } else {
        return value
    }
}

fun main() {
    val input1 = "-f --option=opt key value"
    val input2 = "map=[key1=value1 key2=value2 key3=value3] key=value"
    val input3 = "key value list=(value1 value2 value3)"

    println(parseArguments(input1))
    println(parseArguments(input2))
    println(parseArguments(input3))
}
