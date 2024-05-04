package org.yunghegel.gdx.renderer.shader

import com.badlogic.gdx.graphics.g3d.Attribute
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute

class ShaderPrefix(var prefix: String,val renderable: Renderable) {
    val setters : MutableList<(Shader,Renderable)->Unit>

    constructor(renderable: Renderable) : this("",renderable)

    init {
        setters = mutableListOf()
        processVertexAttributes()
        processTextureAttributes()
    }


    fun addSetter(setter: (Shader,Renderable?)->Unit) : ShaderPrefix {
        setters.add(setter)
        return this
    }

    fun processVertexAttributes() : ShaderPrefix {

        fun resolveAttribute(attribute: String) : String {
//            a_position -> positionFlag
            return attribute.replace("a_","")+ "Flag"
        }

        renderable.meshPart.mesh.vertexAttributes.forEach {
            vertexAttribute -> appendDefine(resolveAttribute(vertexAttribute.alias))
        }
        println(prefix)
        return this
    }

    fun processTextureAttributes() : ShaderPrefix {
        renderable.material.forEach {
            attribute -> if(attribute is TextureAttribute){
                appendDefine(Attribute.getAttributeAlias(attribute.type) + "Flag")
//                when(attribute.type) {
//                    TextureAttribute.Diffuse -> addSetter { shader, _ ->
//                        shader.program.setUniformi(
//                            "u_diffuseTexture",
//                            shader.context.textureBinder.bind(attribute.textureDescription.texture)
//                        )
//                    }
//                    TextureAttribute.Normal -> addSetter { shader, renderable ->
//                        shader.program.setUniformi(
//                            "u_normalTexture",
//                            shader.context.textureBinder.bind(attribute.textureDescription.texture)
//                        )
//                    }
//                    TextureAttribute.Specular -> addSetter { shader, renderable ->
//                        shader.program.setUniformi(
//                            "u_specularTexture",
//                            shader.context.textureBinder.bind(attribute.textureDescription.texture)
//                        )
//                    }
//                    TextureAttribute.Emissive -> addSetter { shader, renderable ->
//                        shader.program.setUniformi(
//                            "u_emissiveTexture",
//                            shader.context.textureBinder.bind(attribute.textureDescription.texture)
//                        )
//                    }
//                    TextureAttribute.Reflection -> addSetter { shader, renderable ->
//                        shader.program.setUniformi(
//                            "u_reflectionTexture",
//                            shader.context.textureBinder.bind(attribute.textureDescription.texture)
//                        )
//                    }
//                    TextureAttribute.Ambient -> addSetter { shader, renderable ->
//                        shader.program.setUniformi(
//                            "u_ambientTexture",
//                            shader.context.textureBinder.bind(attribute.textureDescription.texture)
//                        )
//                    }
//
//                    TextureAttribute.Specular -> addSetter { shader, renderable ->
//                        shader.program.setUniformi(
//                            "u_specularTexture",
//                            shader.context.textureBinder.bind(attribute.textureDescription.texture)
//                        )
//                    }
//                    TextureAttribute.Bump -> addSetter { shader, renderable ->
//                        shader.program.setUniformi(
//                            "u_bumpTexture",
//                            shader.context.textureBinder.bind(attribute.textureDescription.texture)
//                        )
//                    }
//                    TextureAttribute.Reflection -> addSetter { shader, renderable ->
//                        shader.program.setUniformi(
//                            "u_reflectionTexture",
//                            shader.context.textureBinder.bind(attribute.textureDescription.texture)
//                        )
//                    }
//                    TextureAttribute.Ambient -> addSetter { shader, renderable ->
//                        shader.program.setUniformi(
//                            "u_ambientTexture",
//                            shader.context.textureBinder.bind(attribute.textureDescription.texture)
//                        )
//                    }
//
//                }

        }
        }
        return this
    }

    fun appendDefine(define: String) : ShaderPrefix{
        prefix += "#define $define\n"
        return this
    }

    fun get() : String {
        return prefix
    }

}