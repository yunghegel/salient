package org.yunghegel.gdx.utils.ext

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute
import com.badlogic.gdx.graphics.g3d.attributes.SpotLightsAttribute
import com.badlogic.gdx.graphics.g3d.environment.PointLight
import com.badlogic.gdx.graphics.g3d.environment.SpotLight
import com.badlogic.gdx.utils.Array
import net.mgsx.gltf.scene3d.attributes.FogAttribute
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx
import net.mgsx.gltf.scene3d.utils.IBLBuilder



fun Environment.fog(col: Color?, near: Float, far: Float, exponent: Float): Environment{
    set(ColorAttribute(ColorAttribute.Fog, col))
    set(FogAttribute.createFog(near, far, exponent))
    return this
}

fun Environment.lighting(col: Color?, dirx: Float, diry: Float, dirz: Float): Environment {
    val light = DirectionalLightEx()
    light.direction.set(dirx, diry, dirz).nor()
    light.color.set(col)
    add(light)
    return this
}

fun Environment.ibl(): Environment{
    val light = DirectionalLightEx()

    val iblBuilder = IBLBuilder.createOutdoor(light)
    val environmentCubemap = iblBuilder.buildEnvMap(1024)
    val diffuseCubemap = iblBuilder.buildIrradianceMap(256)
    val specularCubemap = iblBuilder.buildRadianceMap(10)
    iblBuilder.dispose()

    val brdfLUT = Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"))
    set(PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT))
    set(PBRCubemapAttribute.createSpecularEnv(specularCubemap))
    set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap))
    return this
}

fun Environment.setFogColor(col: Color?): Environment {
    set(ColorAttribute(ColorAttribute.Fog, col))
    return this
}

fun Environment.setFogEquation(near:Float,far:Float,exponent:Float): Environment {
    val fa: FogAttribute = get<FogAttribute>(FogAttribute::class.java, FogAttribute.FogEquation)
    fa[near, far] = exponent
    return this
}


fun Environment.getPointLights(): Array<PointLight>? {
    val pla: PointLightsAttribute =
        get<PointLightsAttribute>(PointLightsAttribute::class.java, PointLightsAttribute.Type)
            ?: return null

    val lights = Array<PointLight>()
    for (pl in pla.lights) {
        lights.add(pl)
    }
    return lights
}

fun Environment.getSpotLights(): Array<SpotLight>? {
    val sla: SpotLightsAttribute = get<SpotLightsAttribute>(SpotLightsAttribute::class.java, SpotLightsAttribute.Type)
        ?: return null

    val lights = Array<SpotLight>()
    for (sl in sla.lights) {
        lights.add(sl)
    }
    return lights
}

fun Environment.hasLight(light: PointLight?): Boolean {
    val pla: PointLightsAttribute =
        get<PointLightsAttribute>(PointLightsAttribute::class.java, PointLightsAttribute.Type)
            ?: return false

    for (pl in pla.lights) {
        if (pl.equals(light)) return true
    }
    return false
}

fun Environment.hasLight(light: SpotLight?): Boolean {
    val sla: SpotLightsAttribute = get<SpotLightsAttribute>(SpotLightsAttribute::class.java, SpotLightsAttribute.Type)
        ?: return false

    for (sl in sla.lights) {
        if (sl.equals(light)) return true
    }
    return false
}