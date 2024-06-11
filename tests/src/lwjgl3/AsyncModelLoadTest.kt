package lwjgl3

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Align
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import org.yunghegel.gdx.utils.ext.instance
import org.yunghegel.gdx.utils.ext.rand
import org.yunghegel.salient.engine.helpers.Models
import org.yunghegel.salient.engine.helpers.SampleModels
import types.lwjgl3test
import kotlin.system.measureTimeMillis

val AsyncModelLoadTest = lwjgl3test("AsyncModelLoadTest") {

    var options = object {
        var async = true
        var selection = SampleModels.CONE
    }

    suspend fun loadAsync(sample: SampleModels, cb: (Model)->Unit) {
        sample.loadAsync(ext="gltf") { model ->
            model?.let { cb(it) }
        }
    }

    var count = 0

    fun loadSync (sample: SampleModels, cb: (Model)->Unit) {
        sample.load(ext="gltf") { model ->
            model.let { cb(it) }
        }
    }

    fun addModel(model: Model) {
        val i = model.instance
        bundle.instances.add(i)
        val r = rand.vec3(-10f, 10f)
        i.transform.setTranslation(r)
        val randDir = rand.vec3norm()
        i.transform.rotate(Vector3.Y, randDir)
    }

    execCreate = {
        bundle.init()
        var len = 0L
        group {
            button("Load") {
                len = measureTimeMillis {
                    when (options.async) {
                        true -> {
                            KtxAsync.launch {
                                Models.each { model ->
                                    async {
                                        loadAsync(model) { mdl ->
                                            addModel(mdl)
                                        }
                                    }
                                }
                            }
                        }

                        false -> {
                            Models.each { model ->
                                loadSync(model) { mdl ->
                                    addModel(mdl)
                                }
                            }
                        }
                    }
                }

            }
            button("Clear") {
                bundle.instances.clear()

            }
            label(0) {
                "Instances: ${bundle.instances.size} Time: $len"
            }
        }

        row("profile") {
            group {
                label(Align.topLeft)  {
                    "${createProfile}"
                }
                label(Align.topRight) {
                    "${renderProfile.toString()}"
                }
            }
        }





    }

    execRender = {
        bundle.render()

    }
}



   fun main(){
        AsyncModelLoadTest()
    }
