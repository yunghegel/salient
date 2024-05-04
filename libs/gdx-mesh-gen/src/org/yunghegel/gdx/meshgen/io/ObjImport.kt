package org.yunghegel.gdx.meshgen.io

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.*
import org.yunghegel.gdx.meshgen.data.ifs.*

class ObjImport(val path: String) : Importer{


    val model : Model
    val mesh : Mesh

    init {
        model = load()
        mesh = model.meshes.first()
    }

    override fun load() : Model {
        return ModelLoader(path).load()
    }

    override fun toIFS(): IFSMesh {
        val meshImport = IFSMeshConstructor()



        return meshImport.convert(mesh)
    }

    override fun fromIFS(): Model {
        TODO("Not yet implemented")
    }


}
