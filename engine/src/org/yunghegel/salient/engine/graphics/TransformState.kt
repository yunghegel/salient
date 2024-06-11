package org.yunghegel.salient.engine.graphics

import com.badlogic.gdx.graphics.Color

enum class TransformState(val id: Int, val color : Color = Color.WHITE.cpy()) {
    None(0),
    X(1, Color(0.8f,0f,0f,1f)),
    Y(2,Color(0f,.8f,0f,1f)),
    Z(3, Color.valueOf("#0074F7")),
    XY(4),
    XZ(5),
    YZ(6),
    XYZ(7,Color(0f,.8f,.8f,1f));

    companion object {
        fun fromId(id: Int): TransformState {
            return when (id) {
                0 -> None
                1 -> X
                2 -> Y
                3 -> Z
                4 -> XY
                5 -> XZ
                6 -> YZ
                7 -> XYZ
                else -> None
            }
        }
    }
}