package org.yunghegel.gdx.cli.value

import org.yunghegel.gdx.cli.CommandLine
import org.yunghegel.gdx.cli.arg.Command
import org.yunghegel.gdx.cli.arg.Namespace
import org.yunghegel.gdx.cli.arg.Value
import org.yunghegel.gdx.cli.util.Type
import org.yunghegel.gdx.utils.reflection.KAccessor

class CLIValue(val accessor: KAccessor, val value : Value) {

    override fun toString(): String {
        return "${accessor.name}: ${value.type} = ${accessor.get()} (${value.description})"
    }

}

@Namespace("primitives")
class TestValues {

    @Value(Type.STRING, "A string value")
    var string : String = "StringValue"

    @Value(Type.INT, "An integer value")
    var int : Int = 0

    @Value(Type.FLOAT, "A float value")
    var float : Float = 0f

    @Value(Type.BOOLEAN, "A boolean value")
    var bool : Boolean = false

    @Command("sayHello")
    fun sayHello() {
        println("Hello!")
    }

}

fun main() {
    val cli = CommandLine()
    val values = TestValues()
    cli.register(values)
}