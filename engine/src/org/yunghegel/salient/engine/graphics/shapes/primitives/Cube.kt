package org.yunghegel.salient.engine.graphics.shapes.primitives

import com.badlogic.gdx.graphics.Color
import org.yunghegel.salient.engine.graphics.shapes.BuilderUtils

class Cube @JvmOverloads constructor(var size: Float, color: Color? = BuilderUtils.getRandomColor()) :
    Box(size, size, size, color)
