package org.yunghegel.gdx.utils.ext

const val ANSI_RESET = "\u001B[0m"
const val ANSI_BLACK = "\u001B[30m"
const val ANSI_RED = "\u001B[31m"
const val ANSI_GREEN = "\u001B[32m"
const val ANSI_YELLOW = "\u001B[33m"
const val ANSI_BLUE = "\u001B[34m"
const val ANSI_PURPLE = "\u001B[35m"
const val ANSI_CYAN = "\u001B[36m"
const val ANSI_WHITE = "\u001B[37m"

enum class Ansi(val ansi: String) {
    BLACK(ANSI_BLACK),
    RED(ANSI_RED),
    GREEN(ANSI_GREEN),
    YELLOW(ANSI_YELLOW),
    BLUE(ANSI_BLUE),
    PURPLE(ANSI_PURPLE),
    CYAN(ANSI_CYAN),
    WHITE(ANSI_WHITE),
    RESET(ANSI_RESET)



}

fun colorize(string: String, color: Ansi): String {
    return color.ansi + string + Ansi.RESET.ansi
}
