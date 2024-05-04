package org.yunghegel.gdx.meshgen.data.dedup

import com.badlogic.gdx.math.*
import org.yunghegel.gdx.meshgen.data.base.*

class ExactHashGridDeduplication<E:Element> (val mesh: StructuredMesh<*,*,*>, val data: ElementData<E>) : ElementDeduplication<E> {

    val grid = HashMap<Int, E>()



    override fun addExisting(e: E) {
        val e = data.elements[e.index]
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

}
