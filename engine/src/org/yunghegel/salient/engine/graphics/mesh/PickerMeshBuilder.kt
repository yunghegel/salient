package org.yunghegel.salient.engine.graphics.mesh

import com.badlogic.gdx.graphics.Mesh
import org.yunghegel.gdx.meshgen.io.IFSMeshConstructor

object PickerMeshBuilder {

    fun create(mesh: Mesh) {
        val ifs = IFSMeshConstructor().convert(mesh)



    }

}