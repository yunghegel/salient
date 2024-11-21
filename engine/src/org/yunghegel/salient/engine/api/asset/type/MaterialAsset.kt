package org.yunghegel.salient.engine.api.asset.type

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g3d.Material
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.api.asset.type.spec.AttributeSpec
import org.yunghegel.salient.engine.api.asset.type.spec.MaterialSpec
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.project.EditorProject
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.helpers.Serializer.yaml
import org.yunghegel.salient.engine.system.file.Filepath
import org.yunghegel.salient.engine.system.file.Paths

class MaterialAsset(val fpath: Filepath) : Asset<Material>(fpath, Material::class.java) {

    constructor(project:String, name: String) : this(Filepath(Paths.PROJECT_ASSET_DIR_FOR(project).toString() + "/$name.material")) {
        handle = AssetHandle(path.toString())
        value = Material()
    }

    constructor(project:String, name: String, material: Material) : this(Filepath(Paths.PROJECT_ASSET_DIR_FOR(project).toString() + "/$name.material")) {
        handle = AssetHandle(path.toString())
        value = material
        material.forEach { attr ->
            val spec = AttributeSpec.fromAttribute(attr)
            attributes.add(spec)
        }

    }

    @Transient
    override val loader = MaterialLoader()

    @Transient
    override var value: Material? = null


    var attributes: MutableList<AttributeSpec> = mutableListOf()


    inner class MaterialLoader : Loader<Material>() {
        override fun resolveHandle(assetHandle: AssetHandle): FileHandle {
            return assetHandle.file.handle
        }
        override fun load(assetHandle: AssetHandle): Material {
            val file = resolveHandle(assetHandle)
            val matspec = yaml.decodeFromString(MaterialSpec.serializer(), file.readString())
            val material = Material()
            matspec.attributes.forEach { spec ->
                val attr = AttributeSpec.toAttribute(spec)
                material.set(attr)
            }
            return material
        }
    }

    companion object {




    }



}