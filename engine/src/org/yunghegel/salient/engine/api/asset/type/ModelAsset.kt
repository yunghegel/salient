package org.yunghegel.salient.engine.api.asset.type

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader
import net.mgsx.gltf.loaders.gltf.GLTFLoader
import org.yunghegel.gdx.utils.data.ID
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.api.asset.AssetUsage
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.ModelRenderable
import org.yunghegel.salient.engine.scene3d.component.*
import org.yunghegel.salient.engine.system.file.Filepath
import org.yunghegel.salient.engine.system.info

class ModelAsset(path: Filepath, val id: ID, handle:AssetHandle) : Asset<Model>(path, Model::class.java,handle), AssetUsage<Model> {

    override fun useAsset(asset: Model, go: GameObject) {
        info("Applying model asset to game object ${go.name}")
        removeAsset(asset, go)
        val pickable = ModelRenderable(ModelInstance(asset), go)
        if(go.getComponent(RenderableComponent::class.java)==null)


        go.add(RenderableComponent(pickable, go))
        go.add(PickableComponent(pickable, go))
        go.add(MaterialsComponent(asset.materials, go))
        go.add(MeshComponent(asset.meshes, go))
        go.add(BoundsComponent(BoundsComponent.getBounds(asset),go))
    }

    override fun removeAsset(asset: Model, go: GameObject) {
        go.remove(RenderableComponent::class.java)
        go.remove(PickableComponent::class.java)
        go.remove(MaterialsComponent::class.java)
        go.remove(MeshComponent::class.java)
        go.remove(BoundsComponent::class.java)
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