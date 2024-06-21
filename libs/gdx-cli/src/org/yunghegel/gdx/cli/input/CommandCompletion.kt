package org.yunghegel.gdx.cli.input

interface CommandCompletion {

    fun completeCommand(string: String)

    fun getOptions(): List<String>

}