package org.yunghegel.gdx.utils

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop

class TypedPayload<T>() : DragAndDrop.Payload() {

    var value: T? = null
        set(value) {
            field = value
            `object` = value
        }
}