package org.yunghegel.gdx.cli

import org.yunghegel.gdx.cli.arg.Arg
import org.yunghegel.gdx.cli.arg.Cmd
import org.yunghegel.gdx.cli.arg.Flag
import org.yunghegel.gdx.cli.arg.Namespace
import java.io.File

@Namespace("global")
class CLIEnvironment : HashMap<String,String>() {

    @Cmd("set","set environment variable")
    fun set(@Arg("key") key: String, @Arg("value") value: String, @Flag("save") save: Boolean = false) {
        this[key] = value
        if (save) {
            saveEnv("env.txt")
        }
    }

    @Cmd("env","print environment variables")
    fun printenv() {
        for ((key, value) in this) {
            println("$key=$value")
        }
    }

    @Cmd("loadenv","load environment from file")
    fun loadEnv(@Arg("file") file: String) {
        val lines = File(file).readLines()
        for (line in lines) {
            val (key, value) = line.split("=", limit = 2)
            this[key] = value
        }
    }

    @Cmd("saveenv","save environment to file")
    fun saveEnv(@Arg("file", "filepath to save to") file: String) {
        File(file).writeText(this.map { (key, value) -> "$key=$value" }.joinToString("\n"))
    }

}