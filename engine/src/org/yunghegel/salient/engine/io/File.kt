package org.yunghegel.salient.engine.sys

import com.badlogic.gdx.files.FileHandle
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.ui.Icons

class File(val absolutePath: String) : FileHandle(absolutePath) {

    internal val filepath : Filepath = Filepath(absolutePath)
    val filetype : FileType = FileType.parse(filepath.extension)
    val icon : Icons = filetype.icon

    val isAsset = FileType.isAsset(filetype)

    var assetHandle : AssetHandle? = null

    init {
        if(isAsset) {
            if (filetype==FileType.ASSET_INDEX) {

            }
        }
    }

    private fun discoverAsset() {

    }

}