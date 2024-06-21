package org.yunghegel.gdx.cli.arg

interface InputParser {

    fun parseArguments(args: Array<String>): ParsedInput

}