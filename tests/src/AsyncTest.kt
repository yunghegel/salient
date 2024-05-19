import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader
import com.badlogic.gdx.utils.Array
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ktx.app.clearScreen
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.async.newAsyncContext
import net.mgsx.gltf.loaders.gltf.GLTFAssetLoader
import net.mgsx.gltf.loaders.gltf.GLTFLoader
import net.mgsx.gltf.scene3d.scene.SceneAsset
import org.yunghegel.gdx.renderer.util.toInternalFile
import org.yunghegel.gdx.utils.ext.instance
import org.yunghegel.salient.engine.helpers.SampleModels
import java.io.File

class AsyncModelLoader : ApplicationAdapter() {


    lateinit var instances : Array<ModelInstance>
    lateinit var cam : PerspectiveCamera
    lateinit var batch : ModelBatch

    val assets : AssetStorage by lazy { AssetStorage(asyncContext = newAsyncContext(threads = 2)) }
    val loaded = mutableListOf<String>()

    init {
        File("models/gltf/").list()?.forEach { file ->
            println(file)  }
    }

    override fun create() {
        KtxAsync.initiate()
        instances = Array()
        cam = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        cam.position.set(10f, 10f, 10f)
        cam.lookAt(0f, 0f, 0f)
        cam.near = 1f
        cam.far = 300f
        cam.update()


        batch = ModelBatch()

        assets.setLoader<SceneAsset>("gltf"){ GLTFAssetLoader() }
        assets.setLoader("obj") { ObjLoader(InternalFileHandleResolver()) }

        KtxAsync.launch {
            SampleModels.entries.forEachIndexed { i, modelRef ->
                loadAsync(modelRef.path("models", "gltf"))
            }
        }




    }

    fun loadFolderAsync(path: String) = KtxAsync.launch {
        val files = path.toInternalFile().list()
        files.forEachIndexed { i, file ->
            val location = "$path/${file.name()}"
            val asset = async { loadAsync(location, this) }
            addModel(asset.await(),i)
        }
    }

    fun loadFolder(path:String) {
        val files = path.toInternalFile().list()
        files.forEachIndexed { i, file ->
            val location = "$path/${file.name()}"
            loadNormal(location,i )
        }
    }

    suspend fun loadAsync(path: String, scope: CoroutineScope=KtxAsync) : SceneAsset {
        val job = scope.launch { println("Loading: ${assets.progress}") }
        val model = assets.load<SceneAsset>(path)
        loaded.add(path)

        return model
    }

    fun loadNormal(path: String,id :Int) {

        val model = GLTFLoader().load(path.toInternalFile())
        loaded.add(path)
        addModel(model,id)
    }

    fun addModel(sceneAsset: SceneAsset,id: Int) {
        val instance = sceneAsset.scene.model.instance
        instance.transform.setToTranslation(id*2f,0f,0f)
        instances.add(instance)


    }

    override fun render() {
        clearScreen(0.1f,0.1f,0.1f,0f)

            batch.begin(cam)
            batch.render(instances)
            batch.end()



    }


}

fun main() { Lwjgl3Application(AsyncModelLoader(), Lwjgl3ApplicationConfiguration().apply {setWindowedMode(800,600)}) }