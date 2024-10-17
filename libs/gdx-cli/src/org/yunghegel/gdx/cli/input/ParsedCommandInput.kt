package org.yunghegel.gdx.cli.input

import org.yunghegel.gdx.cli.cmd.CLICommand
import org.yunghegel.gdx.cli.util.Kind

data class ParsedCommandInput(val command: CLICommand, override val arguments: Map<String, String>, val options: Map<String, String>, val flags: Set<String>) : ParsedInput {
    override val kind = Kind.COMMAND

}

