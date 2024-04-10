package org.yunghegel.gdx.utils.primitives

import com.badlogic.gdx.graphics.Color

class CubeSupplier @JvmOverloads constructor(size: Float, color: Color? = BuilderUtils.getRandomColor()) :
    BoxSupplier(size, size, size, color)
