package org.yunghegel.salient.engine.scene3d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Cubemap
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.graphics.g3d.utils.ShapeCache
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx
import net.mgsx.gltf.scene3d.scene.SceneRenderableSorter
import net.mgsx.gltf.scene3d.shaders.PBRDepthShaderProvider
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider
import net.mgsx.gltf.scene3d.utils.IBLBuilder
import org.yunghegel.salient.engine.api.dto.datatypes.DirectionalLightData
import org.yunghegel.salient.engine.graphics.debug.DebugContext
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.api.scene.SceneEnvironment
import org.yunghegel.salient.engine.graphics.BasicRenderer
import org.yunghegel.salient.engine.graphics.GFX
import org.yunghegel.salient.engine.graphics.GraphicsContext
import org.yunghegel.salient.engine.graphics.SharedGraphicsResources
import org.yunghegel.salient.engine.helpers.BlinnPhongBatch
import org.yunghegel.salient.engine.helpers.DepthBatch
import org.yunghegel.salient.engine.helpers.PBRBatch
import org.yunghegel.salient.engine.helpers.WireBatch
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.system.register
import org.yunghegel.salient.engine.system.singleton


class SceneContext(private var scene:EditorScene) : SceneEnvironment(), Disposable, GraphicsContext by GFX {


    val renderer : BasicRenderer

    val dirLight: DirectionalLightEx = DirectionalLightData().fromDTO(DirectionalLightData.default)

    var current: Camera



    init {



        current = perspectiveCamera

        renderer= BasicRenderer(perspectiveCamera,this)
        initLighting()
        supplyDependencies()
    }


    fun supplyDependencies() {

        singleton<Viewport>(viewport)

        register {
//            bind(Camera::class) {
//                if (current is OrthographicCamera) {
//                    orthographicCamera
//                } else {
//                    perspectiveCamera
//                }
//            }
        }

    }

    fun initLighting() {

        environment.add(dirLight)

        val iblBuilder = IBLBuilder.createOutdoor(dirLight);
        val diffuseCubemap = iblBuilder.buildIrradianceMap(512);
        val specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        val tex =
            (environment.get(PBRTextureAttribute.BRDFLUTTexture) as PBRTextureAttribute?)?.textureDescription?.texture;
        if (tex == null) {
            val brdfLUT = Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));
            environment.set(PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        }

        val specularEnv = environment.get(PBRCubemapAttribute.SpecularEnv) as PBRCubemapAttribute?;
        if (specularEnv != null) {
            specularEnv.textureDescription.texture.dispose();
            specularEnv.textureDescription.texture = specularCubemap;
        } else {
            environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        }

        val diffuseEnv = environment.get(PBRCubemapAttribute.DiffuseEnv) as PBRCubemapAttribute?;
        if (diffuseEnv != null) {
            diffuseEnv.textureDescription.texture.dispose();
            diffuseEnv.textureDescription.texture = diffuseCubemap;
        } else {
            environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));
        }
    }

    fun conf (action : RenderContext.()->Unit) {
        Companion.action()
    }

    fun set(scene:EditorScene) {
        this.scene = scene
        deriveContext(scene)
    }

    fun deriveContext(scene:EditorScene) {

    }

    fun resize (width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    companion object : RenderContext(DefaultTextureBinder(DefaultTextureBinder.ROUNDROBIN));

    override fun dispose() {
        modelBatch.dispose()
    }


}