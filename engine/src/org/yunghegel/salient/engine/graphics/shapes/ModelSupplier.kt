package org.yunghegel.salient.engine.graphics.shapes

import com.badlogic.gdx.graphics.g3d.Model

interface ModelSupplier {
    fun createModel(): Model?

    fun createModel(parameters: ShapeParameters) : PrimitiveModel {
        return PrimitiveModel(parameters, createModel()!!)
    }
}
