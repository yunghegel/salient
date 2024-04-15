package org.yunghegel.salient.engine.api.asset.type

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.glutils.ShaderPart
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShaderStage
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.system.file.Filepath

class ShaderAsset(basedir: Filepath, val name:String) : Asset<ShaderProgram>(basedir, ShaderProgram::class.java) {

    enum class ShaderType(val stage:ShaderStage,val extensions: Array<String>) {
        VERTEX(ShaderStage.vertex,arrayOf("vert", "vs")),
        FRAGMENT(ShaderStage.fragment,arrayOf("frag", "fs")),
        GEOMETRY(ShaderStage.geometry,arrayOf("geom", "gs"))
    }

    val types = arrayOf(ShaderType.VERTEX, ShaderType.FRAGMENT, ShaderType.GEOMETRY)

    override val loader = object : Loader<ShaderProgram>() {
        override fun resolveHandle(assetHandle: AssetHandle): FileHandle {
            return FileHandle(assetHandle.path.path)
        }

        override fun load(assetHandle: AssetHandle): ShaderProgram {
            var parts = mutableListOf<ShaderPart>()
            types.forEach {
                val file = resolveHandle(assetHandle)
                val part = locateInDirectory(file.parent(), it)
                if (part != null) {
                    parts.add(part)
                }
            }
            val shaderProgram = ShaderProgram(*parts.toTypedArray())
            return shaderProgram
        }

        fun locateInDirectory(directory: FileHandle, type: ShaderType) : ShaderPart? {
            directory.list().forEach { file ->
                if (file.extension() in type.extensions) {
                    if (file.nameWithoutExtension() == name) {
                        return ShaderPart(type.stage, file.readString())
                    }
                }
            }
            return null
        }
    }


}