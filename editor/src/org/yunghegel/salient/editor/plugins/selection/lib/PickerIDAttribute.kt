package org.yunghegel.salient.editor.plugins.selection.lib

import com.badlogic.gdx.graphics.g3d.Attribute

class PickerIDAttribute : Attribute {

    var r: Int = 255

    var g: Int = 255

    var b: Int = 255

    constructor() : super(Type)

    constructor(other: PickerIDAttribute?) : super(Type)

    override fun copy(): PickerIDAttribute {
        return PickerIDAttribute(this)
    }

    override fun hashCode(): Int {
        return r + g * 255 + b * 255 * 255
    }

    override fun compareTo(o: Attribute): Int {
        return -1 // FIXME implement comparing
    }

    override fun toString(): String {
        return "GameObjectIdAttribute{r=$r, g=$g, b=$b}"
    }

    companion object {
        const val Alias: String = "goID"
        val Type: Long = register(Alias)

        fun `is`(mask: Long): Boolean {
            return (mask and Type) == mask
        }
    }
}
