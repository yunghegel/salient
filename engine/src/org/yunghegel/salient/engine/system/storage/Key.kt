package org.yunghegel.salient.engine.system.storage

import kotlinx.serialization.Serializable
import org.yunghegel.gdx.utils.data.Scope

@Serializable
data class Key(val name:  String,  val type:String, val id:Int = nextId()) : Scope {

    companion object {
        var _id = 0
        fun nextId() = _id++
    }

    override val scope: String = name


}
