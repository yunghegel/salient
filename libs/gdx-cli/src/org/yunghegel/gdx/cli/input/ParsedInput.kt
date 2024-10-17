package org.yunghegel.gdx.cli.input

import org.yunghegel.gdx.cli.util.Kind

sealed interface ParsedInput {

    val kind : Kind

    val arguments : Map<String, String>

}