package org.yunghegel.gdx.meshgen.data.dedup

import com.badlogic.gdx.math.*
import org.yunghegel.gdx.meshgen.data.base.*

class HashGridDeplucication<E:Element>(val mesh: StructuredMesh<*,*,*>, el: ElementData<E>) : ElementDeduplication<E> {

    val grid = HashMap<Int, MutableList<Int>>()

    init {
        for (i in 0 until el.totalSize()) {
            val element = el.elements[i]
            val hash = element.hashCode()
            val list = grid.getOrPut(hash) { mutableListOf() }
            list.add(i)
        }
    }

    override fun getOrCreate(pos: Vector3): E {
        TODO("Not yet implemented")
    }

    override fun get(pos: Vector3): E? {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun addExisting(e: E) {
        TODO("Not yet implemented")
    }

}
