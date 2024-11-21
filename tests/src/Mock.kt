import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.Model
import org.yunghegel.salient.editor.project.ProjectManager
import org.yunghegel.salient.editor.scene.SceneManager
import org.yunghegel.salient.engine.api.asset.type.ModelAsset
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.model.ProjectHandle
import org.yunghegel.salient.engine.graphics.GFX
import org.yunghegel.salient.engine.input.Input
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.component.ModelComponent
import org.yunghegel.salient.engine.system.file.Filepath
import org.yunghegel.salient.engine.system.singleton

object Mock {

    val projectmangeer = ProjectManager().also { singleton(it) }
    val project = projectmangeer.createDefault().also { projectmangeer.initialize(it) }

    val scenemanager = SceneManager().also { singleton(it)}
    val scene = scenemanager.createDefault().also { scenemanager.initialize(it,true) }

    val projecthandle = ProjectHandle(project.name, project.file)

    val handle =  AssetHandle("/home/jamie/Desktop/salient/editor/assets/models/gltf/Roundcube.gltf")



    fun initializeTestEnv() {
        singleton(scene)
        singleton(project)
        listOf(GFX, Input).forEach { module ->
            module.initialize()
        }
    }

    val modelAsset = ModelAsset(Filepath(Gdx.files.internal("/home/jamie/Desktop/salient/editor/assets/models/gltf/Roundcube.gltf").path()),handle, handle)

    val gameObject = GameObject("test",scene = scene)

    fun prepareModel(model: Model) : GameObject {
        modelAsset.useAsset(model, gameObject)
        val mc = ModelComponent(modelAsset.handle,gameObject)
        gameObject.add(mc)
        return gameObject
    }


}