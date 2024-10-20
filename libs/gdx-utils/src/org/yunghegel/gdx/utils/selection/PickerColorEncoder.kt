package org.yunghegel.gdx.utils.selection

import com.badlogic.gdx.graphics.Color


object PickerColorEncoder {
    /**
     * Decodes a rgba8888 color code to a game object id.
     *
     * @param rgba8888Code
     * rgba8888 color code
     * @return game object id
     */
    fun decode(rgba8888Code: Int): Int {
        var id = (rgba8888Code and -0x1000000) ushr 24
        id += ((rgba8888Code and 0x00FF0000) ushr 16) * 256
        id += ((rgba8888Code and 0x0000FF00) ushr 8) * 256 * 256

        return id
    }

    /**
     * Encodes a game object id to a GameObjectIdAttribute with rgb channels.
     *
     * @return the game object id, encoded as rgb values
     */
    fun encodeRaypickColorId(id: Int): PickerIDAttribute {
        val goIDa = PickerIDAttribute()
        encodeRaypickColorId(id, goIDa)
        return goIDa
    }

    /**
     * Encodes a id to a GameObjectIdAttribute with rgb channels.
     *
     * @param id
     * id
     * @param out
     * encoded id as attribute
     */
    fun encodeRaypickColorId(id: Int, out: PickerIDAttribute) {
        out.r = id and 0x000000FF
        out.g = (id and 0x0000FF00) ushr 8
        out.b = (id and 0x00FF0000) ushr 16
    }

    object Reserved {
//        id decoded to pure red (1,0,0)
        val RED = Color.toIntBits(255, 0, 0, 255)
        val GREEN = Color.toIntBits(0, 255, 0, 255)
        val BLUE = Color.toIntBits(0, 0, 255, 255)
        val CYAN = Color.toIntBits(0, 255, 255, 255)


    }

}
