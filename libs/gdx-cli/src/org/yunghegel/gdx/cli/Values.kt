package org.yunghegel.gdx.cli

import org.yunghegel.gdx.cli.arg.Namespace
import org.yunghegel.gdx.cli.value.CLIValue

@Namespace("global")
class Values : HashMap<String,HashMap<String,CLIValue>>() {



}