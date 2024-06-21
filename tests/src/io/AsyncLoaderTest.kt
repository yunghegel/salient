package io

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.utils.Align
import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.invokeOnCompletion
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.async.newAsyncContext
import ktx.async.schedule
import net.mgsx.gltf.loaders.gltf.GLTFAssetLoader
import net.mgsx.gltf.scene3d.scene.SceneAsset
import org.yunghegel.gdx.utils.ext.instance
import org.yunghegel.salient.editor.app.App
import org.yunghegel.salient.editor.asset.AssetManager
import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.editor.project.ProjectManager
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.editor.scene.SceneManager
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.system.InjectionContext
import types.HeadlessTest
import types.Lwjgl3Test
import java.io.File
import kotlin.concurrent.thread
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class AsyncLoaderTest : Lwjgl3Test() {

    val assets = AssetManager()
    val app = App()

    var time :Duration = Duration.ZERO
    val assetStorage = AssetStorage(asyncContext = newAsyncContext(threads = 2))
    val reporter = newSingleThreadContext("reporter")
    val multithreaded = newFixedThreadPoolContext(4, "loader")

    init {
        app.registry(InjectionContext)
        assetStorage.setLoader("gltf"){ GLTFAssetLoader() }

    }


    @OptIn(ExperimentalTime::class)
    fun load(threadCount : Int) : Duration {
        val context = newFixedThreadPoolContext(threadCount, "loader")
        val handles = FileHandle("C:\\dev\\projects\\salient\\editor\\assets\\models\\gltf").list().filter{ it.extension()=="gltf" }.map { AssetHandle(it).apply {println("${it.name()}")} }

        return measureTime {
            handles.mapIndexed {i, handle ->
                val model = assetStorage.loadAsync<SceneAsset>(handle.pth)
                KtxAsync.launch(context) {
                    val model = model.await()
                    val ins = ModelInstance(model.scene.model,(-handles.size/2).plus(i).toFloat(),0f,0f)
                    bundle.instances.add(ins)
                    println("Loaded: ${handle.name}")
                }
            }
        }


    }

    fun loadAssets() {
        // Using Kotlin's `async` will ensure that the coroutine is not
        // immediately suspended and assets are scheduled asynchronously,
        // but to take advantage of really parallel asset loading, we have
        // to pass a context with multiple loading threads to AssetStorage:

        val handles = FileHandle("C:\\dev\\projects\\salient\\editor\\assets\\models\\gltf").list().filter{ it.extension()=="gltf" }.map { AssetHandle(it).apply {println("${it.name()}")} }
        val def = handles.map { handle ->
            assetStorage.loadAsync<SceneAsset>(handle.pth).apply {
                invokeOnCompletion {
                       println("Loaded: ${handle.name}")
                }
            }
        }


        spinneri("Threads",2,1,10) {
            KtxAsync.launch {
                assetStorage.dispose { identifier, exception ->
                    // This lambda will be invoked for each encountered disposing error:
                    Gdx.app.error("KTX", "Unable to dispose of asset: $identifier", exception)
                }
                for (handle in handles) {
                    bundle.instances.clear()
                    assetStorage.unload<SceneAsset>(handle.pth)
                }
            }
            time = load(it)
        }



        label(1) {"$time"}



        // Now both assets will be loaded asynchronously, in parallel.

    }



    @OptIn(ExperimentalTime::class)
    override var execCreate: () -> Unit = {
        KtxAsync.initiate()
        val time = measureTime {
            loadAssets()
        }

        println("Time: $time")
    }

    override var execRender: () -> Unit = {
        bundle.draw3D()
    }


}

fun main() {
    AsyncLoaderTest().run()
}