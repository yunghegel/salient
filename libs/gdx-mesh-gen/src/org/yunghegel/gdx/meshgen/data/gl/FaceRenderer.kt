package org.yunghegel.gdx.meshgen.data.gl

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import org.yunghegel.gdx.meshgen.data.FilteredSequence
import org.yunghegel.gdx.meshgen.data.ifs.IFSMesh
import org.yunghegel.gdx.meshgen.data.ifs.IFace

class FaceRenderer(mesh:IFSMesh,cam:Camera) : PrimitiveRenderer<IFace>(mesh,cam)
{
    override fun render(element: IFace)
    {



    }

    override fun configureCache(element: FilteredSequence<IFace>) {

        val faces = element.sequence
        faces.forEach {
            val indices = it.indices
//            b?.setColor(Color.CHARTREUSE)

            for (index in indices) {
                val vert = ifs.vertices().elements[index]
                val pos = vert.position.cpy()
                b?.vertex(pos.x,pos.y,pos.z)
            }

            if (indices.size == 3) {
                b?.triangle(indices[0].toShort(),indices[1].toShort(),indices[2].toShort())
            } else if (indices.size == 4) {
                b?.rect(indices[0].toShort(),indices[1].toShort(),indices[2].toShort(),indices[3].toShort())
            }
        }




    }
}
