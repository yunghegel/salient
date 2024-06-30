package org.yunghegel.salient.engine.system

import ktx.collections.GdxArray
import org.yunghegel.gdx.utils.data.Named
import org.yunghegel.salient.engine.api.ecs.System
import org.yunghegel.salient.engine.api.plugin.Plugin
import org.yunghegel.salient.engine.api.tool.Tool


class Index<T> where T: Named {

    val types = mutableMapOf<Class<out T>,MutableList<T>>()

    val toolKeys = GdxArray<Int>()




    val names = mapOf(
        "tools" to Tool::class.java,
        "systems" to System::class.java,
        "plugins" to Plugin::class.java,
        )



    fun index(type:Class<T>,value:T) {
        if (!types.containsKey(type)) {
            types[type] = mutableListOf()
        }
        types[type]?.add(value)
    }

    fun list(type:Class<out T>):MutableList<T>? {
        return types[type]
    }

    fun List<T>.named (name:String):T? {
        return find { it.name == name }
    }

    fun get(type:Class<T>,name:String):T? {
        return types[type]?.find { it.name == name }
    }





}
inline fun <reified T:Named> Index<T>.get(typename: String, name:String):T? {
    val type = names[typename]
    val members = types[type]
    return members?.find { it.name == name }
}

