package org.yunghegel.gdx.cli.arg

import org.yunghegel.gdx.cli.input.ParsedCommandInput

interface InputParser {

    fun parseArguments(args: Array<String>): ParsedCommandInput

}