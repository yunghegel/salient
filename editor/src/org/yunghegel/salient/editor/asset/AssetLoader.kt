package org.yunghegel.salient.editor.asset


import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.engine.api.asset.type.AssetType
import org.yunghegel.salient.engine.system.file.FileType
import org.yunghegel.salient.engine.system.file.Filepath
import org.yunghegel.salient.engine.system.file.Paths
import org.yunghegel.salient.engine.system.inject

class AssetLoader(val path: Filepath, val project: Project = inject()) {

    val index = assetIndex(project)

    fun resolveExtension() : FileType {
        return FileType.parse(path.extension)
    }

    fun resolveType() : Result<AssetType> {
        val extension = resolveExtension()
        return when(extension) {
            FileType.MODEL -> Result.success(AssetType.Model)
            FileType.TEXTURE -> Result.success(AssetType.Texture)
            else -> Result.failure(Error("Unsupported file type"))
        }
    }

    fun locateHandleIfExists() {

    }

    companion object {
        val assetIndex = { proj: Project -> Paths.PROJECT_ASSET_INDEX_FOR(proj.name) }
    }



}