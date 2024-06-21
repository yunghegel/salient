package org.yunghegel.gdx.cli.cmd

import kotlin.reflect.KFunction

data class CLICommand(val obj: Any, val function: KFunction<*>, val description : String = "")