package org.yunghegel.gdx.imgui

import com.badlogic.gdx.math.Vector3
import imgui.type.ImFloat

class ImGuiVector3 {

    private val vec3 = Vector3()

    val x = ImFloat()
    val y = ImFloat()
    val z = ImFloat()

    fun set(x: Float, y: Float, z: Float) {
        this.x.set(x)
        this.y.set(y)
        this.z.set(z)
    }

    fun set(vector3: ImGuiVector3) {
        this.x.set(vector3.x.get())
        this.y.set(vector3.y.get())
        this.z.set(vector3.z.get())
    }


    val vector3 : Vector3
        get() {
            vec3.set(x.get(),y.get(),z.get())
            return vec3
        }



}