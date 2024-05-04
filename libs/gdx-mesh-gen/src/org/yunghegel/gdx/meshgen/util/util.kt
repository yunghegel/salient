package org.yunghegel.gdx.meshgen.util

import com.badlogic.gdx.math.*
import org.yunghegel.gdx.meshgen.util.Ansi.*

const val ANSI_RESET: String = "\u001B[0m"
const val ANSI_BLACK: String = "\u001B[30m"
const val ANSI_RED: String = "\u001B[31m"
const val ANSI_GREEN: String = "\u001B[32m"
const val ANSI_YELLOW: String = "\u001B[33m"
const val ANSI_BLUE: String = "\u001B[34m"
const val ANSI_PURPLE: String = "\u001B[35m"
const val ANSI_CYAN: String = "\u001B[36m"
const val ANSI_WHITE: String = "\u001B[37m"

fun colorize(string: String, color: Ansi): String {
    return color.ansi + string + ANSI_RESET
}

fun trimVector3(v: Vector3): String {
    return trimFloat(v.x) + ", " + trimFloat(v.y) + ", " + trimFloat(v.z)
}

//null safe trim float to 2 decimal places
fun trimFloat(f: Float): String {
    if (f == f.toInt().toFloat()) return f.toInt().toString()
    //        String string = String.format("%(-2.2f" , f);
    //String.format is not GWT compatible
    var string = f.toString()
    val dotIndex = string.indexOf(".")
    if (dotIndex == -1) return string
    string = string.substring(0, dotIndex + 3)

    //ensure 5 digits
    if (string.length <= 5) string += "0"
    return string
}

fun matchesSuffix(string: String, suffix: String): Boolean {
    val suffixLength = suffix.length
    val stringLength = string.length
    val stringSuffix = string.substring(stringLength - suffixLength)
    return stringSuffix == suffix
}

fun extractFileType(string: String): String {
    val lastDot = string.lastIndexOf(".")
    if (lastDot == -1) return ""
    return string.substring(lastDot + 1)
}

enum class Ansi(val ansi: String) {
    BLACK(ANSI_BLACK),
    RED(ANSI_RED),
    GREEN(ANSI_GREEN),
    YELLOW(ANSI_YELLOW),
    BLUE(ANSI_BLUE),
    PURPLE(ANSI_PURPLE),
    CYAN(ANSI_CYAN),
    WHITE(ANSI_WHITE)
}

inline fun perf(name: String, crossinline block: () -> Unit) {
    val start = System.nanoTime()
    block()
    val end = System.nanoTime()
    val time = (end - start) / 1000000f

    val title = colorize("PERF:",BLUE)
    var info = "[$name finished in $time ms]"

    info = colorize(info, YELLOW)


    println("$title $info")
}



fun main () {
    val a = Vector3()
    val b = 2
    val c = 3
    val d = 4
    val e = 5




}
