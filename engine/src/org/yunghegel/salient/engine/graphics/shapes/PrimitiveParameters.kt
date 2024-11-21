package org.yunghegel.salient.engine.graphics.shapes

import kotlinx.serialization.Serializable
import org.yunghegel.gdx.utils.ext.rand
import org.yunghegel.salient.engine.api.project.EditorProject
import org.yunghegel.salient.engine.system.Log.info
import org.yunghegel.salient.engine.system.file.Paths

@Serializable
class ShapeParameters(val kind: Primitive) {



    val integerParams : MutableMap<String,Int> = mutableMapOf()
    val floatParmas : MutableMap<String,Float> = mutableMapOf()



    fun int(name: String, value: Int = 0) {
        integerParams[name] = value
    }

    fun float(name: String, value: Float = 0f) {
        floatParmas[name] = value
    }

    override fun toString(): String {
        return "ShapeParameters(kind=$kind, integerParams=$integerParams, floatParmas=$floatParmas)"
    }

    companion object {
        fun generatePath(project: EditorProject<*,*>, params: ShapeParameters) : String {
            val folder = Paths.PROJECT_ASSET_DIR_FOR(project.name).child("primitives")
            folder.mkdir()
            val path = folder.path + "/"+"${params.kind.name.lowercase().replace("_","")}_${rand.charseq(5)}.shape"
            info(path)
            return path
        }
    }

}

