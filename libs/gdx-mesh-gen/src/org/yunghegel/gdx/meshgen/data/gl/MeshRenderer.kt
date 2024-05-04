package org.yunghegel.gdx.meshgen.data.gl

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Mesh
import org.yunghegel.gdx.meshgen.data.ifs.IFSMesh
import org.yunghegel.gdx.meshgen.data.meshdata

class MeshRenderer(val mesh: IFSMesh,cam:Camera) {

    private val edgeRenderer: EdgeRenderer = EdgeRenderer(mesh,cam)
    private val vertexRenderer: VertexRenderer = VertexRenderer(mesh,cam)
    private val faceRenderer: FaceRenderer = FaceRenderer(mesh,cam)

    val edges by meshdata(mesh.edges)

    val vertices by meshdata(mesh.vertices)

    val faces by meshdata(mesh.faces)


    fun begin(cam:Camera)  {

        edges.filtered = 0
        vertices.filtered = 0
        faces.filtered = 0

        PrimitiveRenderer.begin(cam)
    }

    fun render() {
        edgeRenderer.render(edges)
        vertexRenderer.render(vertices)
        faceRenderer.render(faces)
    }

    fun end() {
        PrimitiveRenderer.end()
    }

    fun renderMesh(mesh: Mesh, cam:Camera) {

    }




}