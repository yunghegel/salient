package org.yunghegel.gdx.meshgen.data

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder

class VertexInfo() : MeshPartBuilder.VertexInfo() {

    constructor(vertexInfo: MeshPartBuilder.VertexInfo) : this() {
        set(vertexInfo)
    }

    constructor(x : Float, y : Float, z : Float, norX : Float? = null, norY : Float? = null, norZ : Float? = null, u : Float? = null, v : Float? = null, r : Float? = null, g : Float? = null, b : Float? = null, a : Float? = null) : this() {
        set(x, y, z, norX, norY, norZ, u, v, r, g, b, a)
    }
    fun set(x : Float? = null, y : Float? = null, z : Float? = null, norX : Float? = null, norY : Float? = null, norZ : Float? = null, u : Float? = null, v : Float? = null, r : Float? = null, g : Float? = null, b : Float? = null, a : Float? = null) {
        if (x != null && y != null && z != null) setPos(x, y, z)
        if (norX != null && norY != null && norZ != null) setNor(norX, norY, norZ)
        if (u != null && v != null) setUV(u, v)
        if (r != null && g != null && b != null && a != null) setCol(r, g, b, a)

    }






}