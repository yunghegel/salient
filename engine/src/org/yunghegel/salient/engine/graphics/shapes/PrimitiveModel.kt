package org.yunghegel.salient.engine.graphics.shapes

import com.badlogic.gdx.graphics.g3d.Model

class PrimitiveModel(val parameters: ShapeParameters, val model: Model) : Model() {
    override fun dispose() {
        super.dispose()
    }
}