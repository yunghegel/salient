package org.yunghegel.salient.engine.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop

interface DragAndDropActor<T: Actor> {


    val actorClass: Class<T>

    val target : DragAndDrop.Target

    val validDropTargets: List<Class<out DragAndDropActor<out Actor>>>

    val validDropSources : List<Class<out DragAndDropActor<out Actor>>>

    fun shouldAcceptDrop(source: DragAndDropActor<*>): Boolean {
        var valid = false
        validDropSources.forEach {
            if (it.isAssignableFrom(source.actorClass)) {
                valid = true
            }
        }
        return valid
    }

    fun shouldAcceptDrag(target: DragAndDropActor<*>): Boolean {
        var valid = false
        validDropTargets.forEach {
            if (it.isAssignableFrom(target.actorClass)) {
                valid = true
            }
        }
        return valid
    }

    fun onDrop(source: DragAndDropActor<*>,payLoad:DragAndDrop.Payload, x: Float, y: Float)

    fun onDrag(payLoad: DragAndDrop.Payload,x: Float, y: Float)

}