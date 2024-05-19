@file:OptIn(ExperimentalStdlibApi::class)

package org.yunghegel.salient.engine.helpers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader
import ktx.assets.async.AssetStorage
import net.mgsx.gltf.loaders.glb.GLBAssetLoader
import net.mgsx.gltf.loaders.gltf.GLTFAssetLoader
import net.mgsx.gltf.loaders.gltf.GLTFLoader
import net.mgsx.gltf.scene3d.scene.SceneAsset
import org.yunghegel.gdx.renderer.util.toInternalFile
import org.yunghegel.gdx.utils.ext.instance
import org.yunghegel.gdx.utils.ext.randomHue
import org.yunghegel.salient.engine.api.asset.buildParameters
import org.yunghegel.salient.engine.system.async
import java.util.Random

typealias Models = SampleModels

enum class SampleModels(val model: String) {
    CONE("Cone"),
    CUBE("Cube"),
    CYLINDER("Cylinder"),
    GEM("Gem"),
    ICOSPHERE("Icosphere"),
    PYRAMID("Pyramid"),
    ROUND_CUBE("Roundcube"),
    SPHERE("Sphere"),
    SPONGE("Sponge"),
    SPOON("Spoon"),
    SUPERTOROID("SuperToroid"),
    SUZANNE("Suzanne"),
    TORUS("Torus"),
    TORUS_KNOT("TorusKnot"),
    TWISTED_TORUS("TwistedTorus");

    val path : (String, String)->String = {basePath, ext -> "${basePath}/${ext}/${model}.${ext}"}

    fun load(basePath:String = "models/", ext: String = "obj", cb: (Model)->Unit = {}) : Model {
        val path = "${basePath}/${ext}/${model}.${ext}"
        if (ext == "obj") return ObjLoader(InternalFileHandleResolver()).loadModel(Gdx.files.internal(path)).apply {
            materials.first().apply {
                remove(TextureAttribute.Diffuse)
                set(ColorAttribute.createDiffuse(randomHue()))
            }

        }
        else if (ext == "gltf") return GLTFLoader().load(Gdx.files.internal(path)).scene.model
        else throw IllegalArgumentException("Unsupported file type")
    }


    suspend fun loadAsync(basePath: String = "models/", ext: String = "obj", await: suspend (Model?)->Unit) : AssetStorage {
        val path = "${basePath}/${ext}/${model}.${ext}"
        val storage = AssetStorage()
        val model = when (ext) {
            "obj" -> {
                storage.setLoader<Model>(ext) { ObjLoader() }
                storage.loadAsync<Model>(path).await()
            }
            "gltf" -> {
                storage.setLoader<SceneAsset>(ext) { GLTFAssetLoader() }
                storage.loadAsync<SceneAsset>(path).await().scene?.model
            }
            "glb" -> {
                storage.setLoader<SceneAsset>(ext) { GLBAssetLoader() }
                storage.loadAsync<SceneAsset>(path).await().scene?.model

            }
            else -> throw IllegalArgumentException("Unsupported file type")
        }
        await(model)
        return storage
    }


    companion object {
        fun random() : Model {
            val values = entries.toTypedArray()
            val random = Random()
            return values[random.nextInt(values.size)].load()
        }

        fun randomUnique() : Model {
            val values = entries.toMutableList()
            val random = Random()
            return values.removeAt(random.nextInt(values.size)).load()
        }

        fun randomInstance() = random().instance

        fun all() = entries.map { it.load() }

        fun allInstances() = all().map { it.instance }

        fun randomCount(count: Int) = (1..count).map { random() }

        fun randomInstanceCount(count: Int) = (1..count).map { randomInstance() }

        fun each(action: (SampleModels) -> Unit) = entries.forEach(action)


    }
}