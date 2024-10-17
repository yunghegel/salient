package org.yunghegel.gdx.cli.cmd

import org.yunghegel.gdx.cli.input.ParsedCommandInput

interface CommandExecutor {

    context(ParsedCommandInput)
    fun executeCommand() : Any?

}