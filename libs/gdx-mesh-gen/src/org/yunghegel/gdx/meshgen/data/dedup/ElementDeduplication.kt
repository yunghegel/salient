package org.yunghegel.gdx.meshgen.data.dedup

import com.badlogic.gdx.math.*
import org.yunghegel.gdx.meshgen.data.base.*

private val WALK_DIRECTION =
    arrayOf(
        // [8][7][3]
        arrayOf(
            intArrayOf(-1, 0, 0),
            intArrayOf(0, -1, 0),
            intArrayOf(0, 0, -1),
            intArrayOf(-1, -1, 0),
            intArrayOf(-1, 0, -1),
            intArrayOf(0, -1, -1),
            intArrayOf(-1, -1, -1)
               ),  // -X, -Y, -Z
        arrayOf(
            intArrayOf(-1, 0, 0),
            intArrayOf(0, -1, 0),
            intArrayOf(0, 0, 1),
            intArrayOf(-1, -1, 0),
            intArrayOf(-1, 0, 1),
            intArrayOf(0, -1, 1),
            intArrayOf(-1, -1, 1)
               ),  // -X, -Y, +Z
        arrayOf(
            intArrayOf(-1, 0, 0),
            intArrayOf(0, 1, 0),
            intArrayOf(0, 0, -1),
            intArrayOf(-1, 1, 0),
            intArrayOf(-1, 0, -1),
            intArrayOf(0, 1, -1),
            intArrayOf(-1, 1, -1)
               ),  // -X, +Y, -Z
        arrayOf(
            intArrayOf(-1, 0, 0),
            intArrayOf(0, 1, 0),
            intArrayOf(0, 0, 1),
            intArrayOf(-1, 1, 0),
            intArrayOf(-1, 0, 1),
            intArrayOf(0, 1, 1),
            intArrayOf(-1, 1, 1)
               ),  // -X, +Y, +Z
        arrayOf(
            intArrayOf(1, 0, 0),
            intArrayOf(0, -1, 0),
            intArrayOf(0, 0, -1),
            intArrayOf(1, -1, 0),
            intArrayOf(1, 0, -1),
            intArrayOf(0, -1, -1),
            intArrayOf(1, -1, -1)
               ),  // +X, -Y, -Z
        arrayOf(
            intArrayOf(1, 0, 0),
            intArrayOf(0, -1, 0),
            intArrayOf(0, 0, 1),
            intArrayOf(1, -1, 0),
            intArrayOf(1, 0, 1),
            intArrayOf(0, -1, 1),
            intArrayOf(1, -1, 1)
               ),  // +X, -Y, +Z
        arrayOf(
            intArrayOf(1, 0, 0),
            intArrayOf(0, 1, 0),
            intArrayOf(0, 0, -1),
            intArrayOf(1, 1, 0),
            intArrayOf(1, 0, -1),
            intArrayOf(0, 1, -1),
            intArrayOf(1, 1, -1)
               ),  // +X, +Y, -Z
        arrayOf(
            intArrayOf(1, 0, 0),
            intArrayOf(0, 1, 0),
            intArrayOf(0, 0, 1),
            intArrayOf(1, 1, 0),
            intArrayOf(1, 0, 1),
            intArrayOf(0, 1, 1),
            intArrayOf(1, 1, 1)
               ),  // +X, +Y, +Z
           )

interface ElementDeduplication<E:Element> {

    fun getOrCreate(pos:Vector3) : E

    fun get(pos:Vector3) : E?

    fun addExisting(e:E)

    fun clear()

}
