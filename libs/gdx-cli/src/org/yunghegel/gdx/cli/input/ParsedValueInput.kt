package org.yunghegel.gdx.cli.input

import org.yunghegel.gdx.cli.util.Kind
import org.yunghegel.gdx.cli.util.ValueAction
import org.yunghegel.gdx.cli.value.CLIValue

data class ParsedValueInput(val cliVal: CLIValue, val action: ValueAction, override val arguments: Map<String, String>) : ParsedInput {
    override val kind = Kind.VALUE
}