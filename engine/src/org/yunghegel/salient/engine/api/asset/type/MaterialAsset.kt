package org.yunghegel.salient.engine.api.asset.type

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g3d.Material
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.api.asset.type.spec.MaterialSpec
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.project.EditorProject
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.helpers.Serializer.yaml
import org.yunghegel.salient.engine.system.file.Filepath
import org.yunghegel.salient.engine.system.file.Paths

class MaterialAsset(path: Filepath) : Asset<Material>(path, Material::class.java) {

    constructor(project:String, name: String) : this(Filepath(Paths.PROJECT_ASSET_DIR_FOR(project).toString() + "/$name.material")) {
        handle = AssetHandle(path.toString())
        value = Material()
    }

    constructor(project:String, name: String, material: Material) : this(Filepath(Paths.PROJECT_ASSET_DIR_FOR(project).toString() + "/$name.material")) {
        handle = AssetHandle(path.toString())
        value = material
        spec = MaterialSpec.fromMaterial(material)
    }

    override val loader = MaterialLoader()

    var spec = MaterialSpec()

    inner class MaterialLoader : Loader<Material>() {
        override fun resolveHandle(assetHandle: AssetHandle): FileHandle {
            return assetHandle.path.handle
        }
        override fun load(assetHandle: AssetHandle): Material {
            val file = resolveHandle(assetHandle)
            return Material()
        }
    }

    companion object {
        fun new(project: EditorProject<*,*>, scene: EditorScene, name: String): MaterialAsset {
            val asset =  MaterialAsset(project.name, name)
            asset.value = Material()
            scene.indexAsset(asset.handle)
            asset.export(asset.path.toString(), yaml.encodeToString(MaterialSpec.serializer(), asset.spec))
            return asset
        }



    }



}