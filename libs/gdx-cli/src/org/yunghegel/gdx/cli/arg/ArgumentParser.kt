package org.yunghegel.gdx.cli.arg

import org.yunghegel.gdx.cli.input.InputFormat
import org.yunghegel.gdx.cli.input.ValueKind

interface ArgumentParser {

    val type : Type

    fun parse(kind:ArgumentKind, format: InputFormat, value:ValueKind, input : String) : Any

}