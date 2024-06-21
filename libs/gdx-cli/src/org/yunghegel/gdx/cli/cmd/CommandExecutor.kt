package org.yunghegel.gdx.cli.cmd

import org.yunghegel.gdx.cli.arg.ParsedInput

interface CommandExecutor {

    context(ParsedInput)
    fun executeCommand() : Any?

}