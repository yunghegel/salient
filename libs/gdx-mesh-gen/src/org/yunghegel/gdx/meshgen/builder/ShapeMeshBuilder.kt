/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.yunghegel.gdx.meshgen.builder

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo
import com.badlogic.gdx.math.*
import org.yunghegel.gdx.meshgen.data.ifs.IFSMesh

/** Class to construct a mesh, optionally splitting it into one or more mesh parts. Before you can call any other method you must
 * call [.begin] or [.begin]. To use mesh parts you must call
 * [.part] before you start building the part. The MeshPart itself is only valid after the call to
 * [.end].
 * @author Xoppa
 */
class ShapeMeshBuilder : MeshBuilder() {

    lateinit var ifs: IFSMesh

    var mesh : Mesh? = null



    override fun begin(attributes: VertexAttributes, primitiveType: Int) {
        super.begin(attributes, primitiveType)
        ifs = IFSMesh()

    }

    override fun end(): Mesh? {
        mesh  =  super.end()
        ifs.vertices().forEach {
            val pos = ifs.position.get(it)
            println("${it.index} - ${pos}")
        }
        println(lastIndex())
        return mesh
    }

    override fun vertex(pos: Vector3?, nor: Vector3?, col: Color?, uv: Vector2?): Short {
        val idx = super.vertex(pos, nor, col, uv)
//        ifs.createVertex(idx.toInt(),pos!!)
        return idx
    }

    override fun vertex(vararg values: Float): Short {
        val idx =  super.vertex(*values)
//        ifs.createVertex(idx.toInt(),pos = Vector3(values[0],values[1],values[2]))
        return idx
    }

    override fun vertex(info: VertexInfo?): Short {
        val idx =  super.vertex(info)

//        ifs.createVertex(idx.toInt(),info!!.position.cpy())
        return idx
    }

    override fun triangle(index1: Short, index2: Short, index3: Short) {
        super.triangle(index1, index2, index3)

        ifs.createFace(index1.toInt(),index2.toInt(),index3.toInt())
    }

    override fun rect(corner00: Short, corner10: Short, corner11: Short, corner01: Short) {
        super.rect(corner00, corner10, corner11, corner01)
        ifs.createFace(corner00.toInt(),corner10.toInt(),corner11.toInt(),corner01.toInt())

    }




}
