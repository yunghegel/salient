import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Mesh
import ktx.app.clearScreen
import org.yunghegel.gdx.meshgen.data.gl.MeshRenderer
import org.yunghegel.gdx.meshgen.data.ifs.IFSMesh
import org.yunghegel.gdx.meshgen.io.IFSMeshConstructor
import types.lwjgl3test

val MeshEngineTest = lwjgl3test("MeshEngineTest") { with(bundle) {

    var bm : IFSMesh? = null
    var mesh : Mesh? = null
    var meshrender : MeshRenderer? = null

    execCreate = {
        init2D()
        init3D()
        mesh = model.meshes.first()
        bm = IFSMeshConstructor().convert(mesh!!)
        meshrender = MeshRenderer(bm!!, cam)
    }

    execRender = {
        clearScreen(0.1f, 0.1f, 0.1f, 1f)
        meshrender!!.begin(cam)
        meshrender!!.render()
        meshrender!!.end()

        renderText("FPS: ${Gdx.graphics.framesPerSecond}", 5f, 5f)
    }



}}


