package org.yunghegel.gdx.utils.primitives

import com.badlogic.gdx.graphics.g3d.Model

interface ModelSupplier {
    fun createModel(): Model?
}
