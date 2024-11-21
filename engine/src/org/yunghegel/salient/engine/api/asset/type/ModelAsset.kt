package org.yunghegel.salient.engine.api.asset.type

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader
import imgui.impl.gl.ImplGL3.Companion.mat
import net.mgsx.gltf.data.texture.GLTFTexture
import org.yunghegel.gdx.utils.data.ID
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.api.asset.AssetUsage
import org.yunghegel.salient.engine.api.asset.Extras
import org.yunghegel.salient.engine.api.asset.type.loader.GLTFAssetLoader
import org.yunghegel.salient.engine.api.asset.type.spec.MaterialSpec
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.project.EditorProject
import org.yunghegel.salient.engine.graphics.shapes.Primitive
import org.yunghegel.salient.engine.graphics.shapes.ShapeParameters
import org.yunghegel.salient.engine.helpers.Serializer.yaml
import org.yunghegel.salient.engine.helpers.encodestring
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.ModelRenderable
import org.yunghegel.salient.engine.scene3d.component.*
import org.yunghegel.salient.engine.system.file.Filepath
import org.yunghegel.salient.engine.system.info
import org.yunghegel.salient.engine.system.inject

class ModelAsset(path: Filepath, val id: ID, handle:AssetHandle) : Asset<Model>(path, Model::class.java,handle), AssetUsage<Model> {

    constructor(primitive: Primitive,handle: AssetHandle) : this(handle.file, primitive, handle) {
        val params : ShapeParameters = yaml.decodeFromString(ShapeParameters.serializer(),handle.file.readString)
        val prim = Primitive.fromParams(params)
        value = prim.model

    }

//    var data : GLTFAssetLoader.GLTFAsset? = null

    override fun useAsset(asset: Model, go: GameObject) {
        value = asset
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
            return assetHandle.file.handle
        }

        override fun load(assetHandle: AssetHandle): Model {
            if (assetHandle.uuid.startsWith("primitive")) {
//                ordinal is stored as the last character of the uuid
                val ordinal = assetHandle.uuid.last().toString().toInt()
                val extras : Extras = yaml.decodeFromString(Extras.serializer(),assetHandle.file.readString)
                println(assetHandle.file.readString)

                val params = yaml.decodeFromString(ShapeParameters.serializer(),extras.map["params"] as String)
                println(params)
                val primitive = Primitive.fromParams(params)
                return primitive.model
            }

            val filehandle = resolveHandle(assetHandle)
            val model = when (filehandle.extension()) {
                "gltf" -> loadGltf(filehandle)
                "obj" ->  loadObj(filehandle)
                else -> throw IllegalArgumentException("Unsupported model format")
            }
            model.materials.forEach { m ->
                    val spec = MaterialSpec.fromMaterial(m)
                val project = inject<EditorProject<*,*>>()
                val materialAsset = MaterialAsset(project.name,m.id,m)
                materialAsset.export(materialAsset.path.path,encodestring(spec))


            }
            return model
        }

        private fun loadGltf(filehandle: FileHandle): Model {
            val loader = GLTFAssetLoader(filehandle)
            val (gltf, sceneAsset) = loader.load()
            loader.textureMap.forEach { t, u ->
                if (u.exists()) {
                    addDependency(AssetHandle(u))
                }
            }


            val materialTextureMap: MutableMap<Int,GLTFTexture> = mutableMapOf()




            return sceneAsset.scene.model
        }

        private fun loadObj(filehandle: FileHandle): Model {
            val objLoader = ObjLoader()
            val model = objLoader.loadModel(filehandle)
            val data = objLoader.loadModelData(filehandle)
            return model
        }





    }
}