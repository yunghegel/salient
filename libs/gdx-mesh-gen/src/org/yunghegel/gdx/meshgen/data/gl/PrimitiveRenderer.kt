package org.yunghegel.gdx.meshgen.data.gl

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.utils.*
import com.badlogic.gdx.graphics.glutils.*
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.*
import org.yunghegel.gdx.meshgen.data.*
import org.yunghegel.gdx.meshgen.data.base.*
import org.yunghegel.gdx.meshgen.data.ifs.*

abstract class PrimitiveRenderer<E: Element>(val ifs:IFSMesh,val cam:Camera) {

    var enabled = true
    val shapeCache: ShapeCache = ShapeCache(32000,32000, VertexAttributes(VertexAttribute.Position()),GL30.GL_TRIANGLES)
    var b: MeshPartBuilder? = null

    open fun beginCache() {
        b = shapeCache.begin()
    }

    abstract fun configureCache(element:FilteredSequence<E>)

    open fun endCache() {
        shapeCache.end()
    }


    fun render(sequence: FilteredSequence<E>) {
        beginCache()

        configureCache(sequence)

        endCache()

        batch.render(shapeCache)

        sequence.forEach {
            render(it)
        }
    }

    abstract fun render(element: E)

    companion object {
        val shapeRenderer: ShapeRenderer = ShapeRenderer()
        val batch : ModelBatch = ModelBatch()
        var b: MeshPartBuilder? = null

        init {
            shapeRenderer.setAutoShapeType(true)

        }

        fun begin(cam:Camera) {
            shapeRenderer.begin(Filled)
            batch.begin(cam)
        }

        fun end() {
            shapeRenderer.end()
            batch.end()
        }


    }

}
