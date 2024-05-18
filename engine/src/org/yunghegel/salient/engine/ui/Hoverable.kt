package org.yunghegel.salient.engine.ui

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import ktx.ashley.addComponent
import org.yunghegel.salient.engine.scene3d.component.BoundsComponent.Companion.getBounds

abstract class Hoverable<T>(val obj: T) : Entity() {


    private val listeners: MutableList<HoverListener<T>>  = mutableListOf()

    enum class Event {
        ENTERED, EXITED, HOVERING
    }

    var hovered : Boolean = false
        set (value) {
            if (field != value) {
                field = value
                if (value) {
                    handleEvent(Event.ENTERED)
                } else {
                    handleEvent(Event.EXITED)
                }
            }
            if (field == value && value) {
                handleEvent(Event.HOVERING)
            }
            field = value
        }


    fun handleEvent(event: Event) {
        var handled = false
        for (listener in listeners) {
            handled = handle(listener,event)
            if (handled) return
        }

    }

    fun handle (listener: HoverListener<T>,event:Event) : Boolean {
        return when (event) {
            Event.ENTERED -> listener.entered()
            Event.EXITED -> listener.exited(obj)
            Event.HOVERING -> listener.hovering(obj)
        }
    }

    fun addListener(listener: HoverListener<T>) {
        listeners.add(listener)
    }

    fun removeListener(listener: HoverListener<T>) {
        listeners.remove(listener)
    }



    abstract fun isHovered(x: Float, y: Float): Boolean

    fun poll(x:Float,y:Float) : Boolean {
        val (x, y) = Gdx.input.x to Gdx.input.y
        hovered = isHovered(x.toFloat(), y.toFloat())
        return hovered
    }



    interface HoverListener<T>  {
        fun entered() : Boolean
        fun exited(obj: T) : Boolean
        fun hovering(obj: T) : Boolean
    }

    interface HoverQueryable {

        var isHovered : (x:Float, y:Float)->Boolean

        fun makeHoverable (engine: Engine) : Hoverable<HoverQueryable> {
            return object : Hoverable<HoverQueryable>(this) {

                init {
                    engine.addEntity(this)
                    add(object : HoverComponent {})
                }

                override fun isHovered(x: Float, y: Float): Boolean {
                    return obj.isHovered(x, y)
                }
            }
        }

    }

    interface HoverComponent : Component


}