package org.yunghegel.salient.engine.ui

interface Icon {

    val iconName : String



}

fun icon(name:String) : Icon {
    return object : Icon {
        override val iconName: String = name
    }
}