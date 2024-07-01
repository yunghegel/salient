package lwjgl3

import Mock
import com.badlogic.gdx.Gdx
import org.yunghegel.salient.editor.plugins.gizmos.systems.GizmoSystem
import org.yunghegel.salient.editor.plugins.gizmos.tools.RotateTool
import org.yunghegel.salient.engine.scene3d.GameObject
import types.lwjgl3test



val test = lwjgl3test("PickingTest") {

    lateinit var rotateGizmo : RotateTool
    lateinit var gizmoSystem : GizmoSystem
    lateinit var target : GameObject

    execCreate= {
        bundle.init3D()
        Mock.initializeTestEnv()
        gizmoSystem = GizmoSystem()
        rotateGizmo = RotateTool(gizmoSystem)
        target = Mock.prepareModel(instance.model)
        gizmoSystem.activeGizmo = rotateGizmo
        rotateGizmo.activate()
    }

    execRender = {
        bundle.draw3D()
//        gizmoSystem.update(Gdx.graphics.deltaTime)
        rotateGizmo.update(Gdx.graphics.deltaTime,target)
        rotateGizmo.render(bundle.batch,bundle.env)

        if(Gdx.input.isButtonPressed(0)) {
            val x = Gdx.input.x
            val y = Gdx.graphics.height - Gdx.input.y
            rotateGizmo.pickHandles(x,y)
        }


    }

}

object PickingTest {
    @JvmStatic
    fun main(args: Array<String>) {
        test()
    }
}