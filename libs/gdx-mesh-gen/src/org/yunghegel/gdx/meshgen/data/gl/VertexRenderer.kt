package org.yunghegel.gdx.meshgen.data.gl

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.math.*
import org.yunghegel.gdx.meshgen.data.*
import org.yunghegel.gdx.meshgen.data.ifs.*

class VertexRenderer(mesh:IFSMesh,cam: Camera) : PrimitiveRenderer<IVertex>(mesh,cam)
{

    val matrix = Matrix4().setToOrtho2D(0f,0f,cam.viewportWidth,cam.viewportHeight)

    override fun render(element: IVertex)
    {
        val pos = cam.project(element.position.cpy() )
        shapeRenderer.setColor(Color.PINK)
        shapeRenderer.projectionMatrix = matrix
        shapeRenderer.circle(pos.x,pos.y,4f)

    }

    override fun configureCache(element: FilteredSequence<IVertex>) {

    }
}
