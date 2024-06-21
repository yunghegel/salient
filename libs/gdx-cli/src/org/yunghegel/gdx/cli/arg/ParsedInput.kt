package org.yunghegel.gdx.cli.arg

import org.yunghegel.gdx.cli.cmd.CLICommand

data class ParsedInput(val command: CLICommand,  val arguments: Map<String, String>, val options: Map<String, String>, val flags: Set<String>)
