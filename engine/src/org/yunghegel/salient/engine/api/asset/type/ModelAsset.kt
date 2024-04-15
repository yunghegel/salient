package org.yunghegel.salient.engine.api.asset.type

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader
import net.mgsx.gltf.loaders.gltf.GLTFLoader
import org.yunghegel.salient.engine.api.ID
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.api.asset.AssetUsage
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.graphics.scene3d.GameObject
import org.yunghegel.salient.engine.graphics.scene3d.component.MaterialsComponent
import org.yunghegel.salient.engine.graphics.scene3d.component.MeshComponent
import org.yunghegel.salient.engine.graphics.scene3d.component.RenderableComponent
import org.yunghegel.salient.engine.system.file.Filepath
import org.yunghegel.salient.engine.system.info

class ModelAsset(path: Filepath,val id: ID,handle:AssetHandle) : Asset<Model>(path, Model::class.java,handle), AssetUsage<Model> {

    override fun useAsset(asset: Model, go: GameObject) {
        info("Applying model asset to game object ${go.name}")
        go.add(RenderableComponent(ModelInstance(asset), go))
        go.add(MaterialsComponent(asset.materials, go))
        go.add(MeshComponent(asset.meshes, go))
    }

    override val loader = ModelLoader()

    inner class ModelLoader : Loader<Model>() {

        override fun resolveHandle(assetHandle: AssetHandle): FileHandle {
            return assetHandle.path.handle
        }

        override fun load(assetHandle: AssetHandle): Model {
            val file = resolveHandle(assetHandle)
            if(assetHandle.path.extension == "gltf" || assetHandle.path.extension == "glb")
                return GLTFLoader().load(assetHandle.path.handle).scene.model
            else if (assetHandle.path.extension == "obj")
                return ObjLoader().loadModel(file)
            else
                throw IllegalArgumentException("Unsupported model format")
        }

    }

}