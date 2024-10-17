package org.yunghegel.gdx.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader
import net.mgsx.gltf.loaders.gltf.GLTFLoader
import java.util.Random

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

    fun load(basePath:String = "models/", ext: String = "obj") : Model {
        val path = "${basePath}/${ext}/${model}.${ext}"
        println("Loading model: $path")
        if (ext == "obj") return ObjLoader(InternalFileHandleResolver()).loadModel(Gdx.files.internal(path))
        else if (ext == "gltf") return GLTFLoader().load(Gdx.files.internal(path)).scene.model
        else throw IllegalArgumentException("Unsupported file type")
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

        fun randomInstance() = random().toInstance()

        fun all() = entries.map { it.load() }

        fun allInstances() = all().map { it.toInstance() }

        fun randomCount(count: Int) = (1..count).map { random() }

        fun randomInstanceCount(count: Int) = (1..count).map { randomInstance() }


    }
}