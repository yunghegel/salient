package org.yunghegel.gdx.cli


const val ANSI_RESET: String = "\u001B[0m"
const val ANSI_BLACK: String = "\u001B[30m"
const val ANSI_RED: String = "\u001B[31m"
const val ANSI_GREEN: String = "\u001B[32m"
const val ANSI_YELLOW: String = "\u001B[33m"
const val ANSI_BLUE: String = "\u001B[34m"
const val ANSI_PURPLE: String = "\u001B[35m"
const val ANSI_CYAN: String = "\u001B[36m"
const val ANSI_WHITE: String = "\u001B[37m"

enum class Ansi(val ansi: String) {
    BLACK(ANSI_BLACK),
    RED(ANSI_RED),
    GREEN(ANSI_GREEN),
    YELLOW(ANSI_YELLOW),
    BLUE(ANSI_BLUE),
    PURPLE(ANSI_PURPLE),
    CYAN(ANSI_CYAN),
    WHITE(ANSI_WHITE);

    fun colorize(text: String): String {
        return "$ansi$text$ANSI_RESET"
    }
}

fun String.green(): String {
    return Ansi.GREEN.colorize(this)
}

fun String.red(): String {
    return Ansi.RED.colorize(this)
}

fun String.yellow(): String {
    return Ansi.YELLOW.colorize(this)
}

fun String.blue(): String {
    return Ansi.BLUE.colorize(this)
}

fun String.purple(): String {
    return Ansi.PURPLE.colorize(this)
}

fun String.cyan(): String {
    return Ansi.CYAN.colorize(this)
}

fun String.white(): String {
    return Ansi.WHITE.colorize(this)
}