package org.yunghegel.gdx.meshgen.io

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader
import com.badlogic.gdx.utils.JsonReader
import net.mgsx.gltf.loaders.glb.GLBLoader
import net.mgsx.gltf.loaders.gltf.GLTFLoader
import kotlin.reflect.KClass

class ModelLoader(val fileHandle: FileHandle) {

    private enum class Loaders(val clazz: KClass<*>) {
        GLTF(GLTFModelLoader::class), GLB(GLBModelLoader::class), OBJ(OBJModelLoader::class), G3D(G3DModelLoader::class)
    }

    constructor(path: String) : this(Gdx.files.internal(path))

    val validTypes = listOf("gltf","glb","obj","g3d")

    fun negotiateLoader() : Loader<*> {

        val ext = fileHandle.extension()
        if (!validTypes.contains(ext)) {
            throw Exception("Invalid file type: $ext")
        }

        val loader = when (ext) {
            "gltf" -> Loaders.GLTF
            "glb" -> Loaders.GLB
            "obj" -> Loaders.OBJ
            "g3d" -> Loaders.G3D
            else -> throw Exception("Invalid file type: $ext")
        }
        val loaderObj = loader.clazz.java.getDeclaredConstructor().newInstance() as Loader<*> ?: throw Exception("Could not instantiate loader")
        return loaderObj
    }

    fun load() : Model {
        return negotiateLoader().load(fileHandle)
    }

}

interface Loader<T> {

    fun load(fileHandle:FileHandle): Model

}

class GLTFModelLoader : Loader<GLTFLoader> {



    override fun load(fileHandle: FileHandle): Model {
        return GLTFLoader().load(fileHandle).scene.model
    }
}

class GLBModelLoader : Loader<GLBLoader> {
    override fun load(fileHandle: FileHandle): Model {
        return GLBLoader().load(fileHandle).scene.model
    }
}

class OBJModelLoader : Loader<ObjLoader> {
    override fun load(fileHandle: FileHandle): Model {
        return ObjLoader().loadModel(fileHandle)
    }
}

class G3DModelLoader : Loader<G3dModelLoader> {
    override fun load(fileHandle: FileHandle): Model {
        return G3dModelLoader(JsonReader()).loadModel(fileHandle)
    }
}

