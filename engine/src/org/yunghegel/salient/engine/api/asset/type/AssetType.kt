@file:OptIn(ExperimentalStdlibApi::class)

package org.yunghegel.salient.engine.api.asset.type

import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.system.file.FileType
import org.yunghegel.salient.engine.ui.UI

enum class AssetType(val filetype: FileType,assetClass: Class<out Asset<*>>) {

    Model(FileType.MODEL, ModelAsset::class.java),
    Texture(FileType.TEXTURE, TextureAsset::class.java),
    Material(FileType.MATERIAL, MaterialAsset::class.java),
    Shader(FileType.SHADER, ShaderAsset::class.java),
    Other(FileType.OTHER, Asset::class.java);

    companion object {
        fun fromFiletype(filetype: FileType): AssetType {
            return entries.first { it.filetype == filetype }
        }

        fun iconFor(filetype: AssetType): Drawable {
            return when (filetype) {
                Model -> UI.drawable("model_file")
                Texture -> UI.drawable("image_icon")
                Material -> UI.drawable("material_file")
                Shader -> UI.drawable("Script")
                Other -> UI.drawable("File")
            }
        }

        val allExt = getAllExtensions()

        private fun getAllExtensions() : Array<String> {
            val listOfArrays =  entries.map { it.filetype.extensions }
            val finalSize = listOfArrays.sumOf { it.size }
            val finalArray = Array(finalSize) { "" }
            var index = 0
            for (array in listOfArrays) {
                for (extension in array) {
                    finalArray[index] = extension
                    index++
                }
            }
            return finalArray
        }

    }

}