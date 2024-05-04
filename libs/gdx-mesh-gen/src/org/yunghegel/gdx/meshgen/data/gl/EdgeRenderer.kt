package org.yunghegel.gdx.meshgen.data.gl

import com.badlogic.gdx.graphics.Camera
import org.yunghegel.gdx.meshgen.data.FilteredSequence
import org.yunghegel.gdx.meshgen.data.ifs.IEdge
import org.yunghegel.gdx.meshgen.data.ifs.IFSMesh

class EdgeRenderer(mesh:IFSMesh,cam:Camera) : PrimitiveRenderer<IEdge>(mesh,cam) {

    override fun render(element: IEdge) {

    }

    override fun configureCache(element: FilteredSequence<IEdge>) {

    }
}