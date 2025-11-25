package org.yunghegel.gdx.util

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider
import com.badlogic.gdx.math.Vector3
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight
import net.mgsx.gltf.scene3d.scene.SceneManager
import net.mgsx.gltf.scene3d.scene.SceneSkybox
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider
import net.mgsx.gltf.scene3d.utils.IBLBuilder

open class TestApplication (val autoExit : Boolean = true, val render: (Any?)->Unit, val createContext: ()->Any?) : ApplicationAdapter() {


    var context : Any? = null

    override fun create() {
        context = createContext()
    }

    override fun render() {
        if(autoExit) Gdx.app.exit()
        else {
            Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT or GL20.GL_COLOR_BUFFER_BIT)
            render(context)
        }
    }

    fun quit() {
        Gdx.app.exit()
    }

    fun run(conf: Lwjgl3ApplicationConfiguration.()->Unit = {}) {
        Lwjgl3Application(this, create(conf))
    }

    companion object Config : Lwjgl3ApplicationConfiguration() {
        fun create(conf: Lwjgl3ApplicationConfiguration.()->Unit = {}) = Lwjgl3ApplicationConfiguration().apply {
            setTitle("Test Application")
            setWindowedMode(800, 600)
            setOpenGLEmulation(GLEmulation.GL32, 3, 2)
            conf()
        }
    }

}

fun <T: Any> extract(obj: T): Sequence<Any> {
    return obj::class.java.declaredFields.apply { forEach { it.isAccessible = true }}.map { p -> p.get(obj) }.asSequence()
}

inline fun <reified T> useProp(obj: Any, property: String, block: (T)->Unit) {
    val prop = obj::class.java.getDeclaredField(property)
    prop.isAccessible = true
    block(prop.get(obj) as T)
}

fun destructureObject(obj:Any,map: Map<String,Class<*>>, block: (String,Any)->Unit) {
    map.forEach { (k,v) -> block(k,obj::class.java.getDeclaredField(k).apply { isAccessible = true }.get(obj)) }

}

fun setupCamera(camera: Camera, sceneManager: SceneManager) {
    val d = .02f
    camera.near = d
    camera.far = 10000f
    sceneManager.setCamera(camera)
    camera.position[0f, 10f] = -10f
}

fun setupLight(light: DirectionalLightEx, sceneManager: SceneManager) {
    light.direction[0f, -3f] = 0f
    light.intensity = 1f
    light.color[1f, 1f, 1f] = 1f
    sceneManager.environment.add(light)
    sceneManager.setAmbientLight(1f)
}

fun setupIBL(sceneManager: SceneManager, light: DirectionalLightEx = DirectionalLightEx().also  { it.direction[0f, -3f] = 0f
    it.intensity = 1f
    it.color[1f, 1f, 1f] = 1f }) {

    val iblBuilder = IBLBuilder.createOutdoor(light)
    val brdfLUT = Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"))
    val environmentCubemap = iblBuilder.buildEnvMap(1024)
    val diffuseCubemap = iblBuilder.buildIrradianceMap(256)
    val specularCubemap = iblBuilder.buildRadianceMap(10)

    sceneManager.setAmbientLight(1f)
    sceneManager.environment.set(PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT))
    sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap))
    sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap))

    val skybox = SceneSkybox(environmentCubemap)
    sceneManager.skyBox = skybox
}

fun quickSceneManagerSetup(): SceneManager {
    val config = PBRShaderConfig()
    config.numBones = 128
    config.numDirectionalLights = 5
    config.numPointLights = 4
    config.numSpotLights = 4
    val depthShaderProvider = DepthShaderProvider()
    depthShaderProvider.config.numBones = 128
    depthShaderProvider.config.numDirectionalLights = 4
    depthShaderProvider.config.numPointLights = 4
    depthShaderProvider.config.numSpotLights = 4
    val sceneManager = SceneManager(PBRShaderProvider(config), depthShaderProvider)


    return sceneManager
}

fun getDirectionalLight(env: Environment): DirectionalLight? {
    val dirLightAttribs = env.get(DirectionalLightsAttribute::class.java, DirectionalLightsAttribute.Type)
    val dirLights = dirLightAttribs.lights
    if (dirLights != null && dirLights.size > 0) {
        return dirLights.first()
    }
    return null
}

fun createBasicEnv(camera: PerspectiveCamera, sceneManager: SceneManager, light: DirectionalShadowLight) {
    val ambientLightColor = Color.WHITE
    val lightDirection = Vector3(-1f, -0.8f, -0.2f)
    val d = .02f
    camera.near = .1f
    camera.far = 5000f
    sceneManager.setCamera(camera)
    camera.position[0f, 1f] = 0f
    light.direction.set(1f, -3f, 1f).nor()
    light.color.set(ambientLightColor)
    light.intensity = 10f
    sceneManager.environment.add(light)
    //sceneManager.environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, lightDirection));
    sceneManager.environment.set(ColorAttribute(ColorAttribute.AmbientLight, ambientLightColor))
    sceneManager.setAmbientLight(.1f)
}

/**
 * Initializes image based lighting with cubemaps, adds skybox and sets environment.
 */
fun applyLighting(
    sceneManager: SceneManager,
    diffuseCubemap: Cubemap?,
    environmentCubemap: Cubemap?,
    specularCubemap: Cubemap?,
    shadowLight: DirectionalShadowLight?
) {
    val brdfLUT = Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"))
    val skybox = SceneSkybox(environmentCubemap)

    sceneManager.environment.set(PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT))
    sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap))
    sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap))
    sceneManager.skyBox = skybox
}