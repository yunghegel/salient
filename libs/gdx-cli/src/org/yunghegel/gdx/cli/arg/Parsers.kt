package org.yunghegel.gdx.cli.arg

import org.yunghegel.gdx.cli.input.InputFormat
import org.yunghegel.gdx.cli.input.ValueKind

class Parsers {

    fun parser(type:Type, parseer: (ArgumentKind, InputFormat, ValueKind, String)->Unit) : ArgumentParser {
        return object : ArgumentParser {
            override val type: Type = type
            override fun parse(kind: ArgumentKind, format: InputFormat, value: ValueKind, input: String) {
                parseer(kind, format, value, input)
            }
        }
    }

    fun stringParser() : ArgumentParser {
        return parser(Type.STRING) { kind, format, value, input ->
            when (value) {
                ValueKind.SINGLE -> input
                else -> throw IllegalArgumentException("Invalid value kind for string parser: $value")
            }
        }
    }





}