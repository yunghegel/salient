package org.yunghegel.gdx.cli.input

class CommandHistory {

    private val commandHistory = mutableListOf<String>()

    var max = 100

    fun addCommand(command: String) {
        if (commandHistory.size >= max) {
            commandHistory.removeAt(0)
        }
        commandHistory.add(command)
    }

    fun getCommands(): List<String> {
        return commandHistory
    }

    fun load(file: String) {
        commandHistory.clear()
        commandHistory.addAll(java.io.File(file).readLines())
    }

    fun save(file: String) {
        java.io.File(file).writeText(commandHistory.joinToString("\n"))
    }

}