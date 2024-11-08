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

val background = "#1d1d1d"

val gutter = "#353535"

val selection = "#2c2c2c"

val text = "#69b48e"

val comment = "#5a5852"

val builtin = "#4a9976"

val keyword = "#218d89"

val directive = "#7081b6"

val number = "#dc903f"

val type = "#b45a31"

val string = "#5ab977"

enum class Ansi(val ansi: String) {


    BLACK(ANSI_BLACK),
    RED(ANSI_RED),
    GREEN(ANSI_GREEN),
    YELLOW(ANSI_YELLOW),
    BLUE(ANSI_BLUE),
    PURPLE(ANSI_PURPLE),
    CYAN(ANSI_CYAN),
    WHITE(ANSI_WHITE),
    RESET(ANSI_RESET);



    companion object {
        val Ansi.hexcode : String get() = when(this) {
            BLACK -> "#333333"
            RED -> "#F48484"
            GREEN -> "#66B395"
            YELLOW -> "#F2D98E"
            BLUE -> "#7098D4"
            PURPLE -> "#B479C3"
            CYAN -> "#6AB8C0"
            WHITE -> "#FFFFFF"
            RESET -> "#000000"
        }

        fun Ansi.wraphex(content:String) = "[${this.hexcode}]$content[]"

    }



}

fun colorize(string: String, color: Ansi): String {
    return color.ansi + string + Ansi.RESET.ansi
}


