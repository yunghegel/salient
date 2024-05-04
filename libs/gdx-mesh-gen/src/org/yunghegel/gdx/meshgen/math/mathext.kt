package org.yunghegel.gdx.meshgen.math

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Vector4

fun Color.toVector3(): Vector3 {
    return Vector3(r, g, b)
}

fun VertexInfo.clear() {
    normal.set(0f, 0f, 0f)
    position.set(0f, 0f, 0f)
    color.set(0f, 0f, 0f, 0f)
    uv.set(0f, 0f)
}

fun Color.toVector4(): Vector4 {
    return Vector4(r, g, b, a)
}
