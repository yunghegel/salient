package org.yunghegel.gdx.meshgen.io

import com.badlogic.gdx.graphics.g3d.Model
import org.yunghegel.gdx.meshgen.data.ifs.IFSMesh

interface Importer {

    fun load() : Model

    fun toIFS() : IFSMesh

    fun fromIFS() : Model

}