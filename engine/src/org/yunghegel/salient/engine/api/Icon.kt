package org.yunghegel.salient.engine.api

interface Icon {

    val iconDrawableName : String



}

fun icon(name:String) :Icon {
    return object : Icon {
        override val iconDrawableName: String = name
    }
}