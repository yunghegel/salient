package org.yunghegel.gdx.meshgen.math.operators

import com.badlogic.gdx.math.Vector3
import org.yunghegel.gdx.meshgen.data.ifs.IFace

class FaceNormal(element: IFace) : AbstractOperation<IFace,Vector3>(element) {

        override fun modify(element: IFace) {
            element.normal = calculate(element)
        }

        override fun calculate(element: IFace): Vector3 {
            val verts = element.vertices
            val v1 = verts[0].position
            val v2 = verts[1].position
            val v3 = verts[2].position

            val v12 = v2.cpy().sub(v1)
            val v13 = v3.cpy().sub(v1)

            val normal = v12.crs(v13)
            normal.nor()

            return normal
        }

}