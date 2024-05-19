package org.yunghegel.gdx.utils

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import org.yunghegel.gdx.utils.ui.Hoverable

class TypedPayload<T>(obj: T? = null) : DragAndDrop.Payload() {



    var hoverable : Hoverable.HoverQueryable? = null

    var value: T? = obj
        set(value) {
            field = value
            `object` = value
        }
}