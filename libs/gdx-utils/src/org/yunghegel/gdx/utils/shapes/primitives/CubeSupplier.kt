package org.yunghegel.gdx.utils.shapes.primitives

import com.badlogic.gdx.graphics.Color
import org.yunghegel.gdx.utils.shapes.BuilderUtils

class CubeSupplier @JvmOverloads constructor(size: Float, color: Color? = BuilderUtils.getRandomColor()) :
    BoxSupplier(size, size, size, color)
