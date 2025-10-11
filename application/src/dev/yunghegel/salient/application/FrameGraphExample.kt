package dev.yunghegel.salient.application

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.viewport.ScreenViewport
import dev.yunghegel.salient.engine.render.framegraph.FrameGraph
import dev.yunghegel.salient.engine.render.framegraph.FrameGraphResource

class FrameGraphExample : ApplicationAdapter() {

    private lateinit var frameGraph: FrameGraph
    private lateinit var batch: SpriteBatch
    private lateinit var texture: Texture
    private lateinit var grayscaleShader: ShaderProgram
    private lateinit var viewport: ScreenViewport

    // Define handles for our render targets
    private val sceneColor = FrameGraphResource("sceneColor")
    private val finalOutput = FrameGraphResource.BACKBUFFER

    override fun create() {
        batch = SpriteBatch()
        texture = Texture("badlogic.jpg") // Make sure you have this image in your assets
        viewport = ScreenViewport()

        // A simple shader that converts a texture to grayscale
        grayscaleShader = ShaderProgram(vertexShader, fragmentShader)
        if (!grayscaleShader.isCompiled) {
            error("Shader failed to compile: ${grayscaleShader.log}")
        }

        // 1. SETUP THE FRAMEGRAPH
        frameGraph = FrameGraph()

        // -- PASS 1: Render the main scene to an FBO --
        frameGraph.addPass(
            name = "Scene Pass",
            setup = {
                // This pass doesn't read from any previous pass
                writesTo(sceneColor, Gdx.graphics.width, Gdx.graphics.height)
            },
            execute = {
                // Clear the FBO with a blue background
                Gdx.gl.glClearColor(0.2f, 0.4f, 0.9f, 1f)
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

                // Render a sprite into the FBO
                batch.projectionMatrix = viewport.camera.combined
                batch.begin()
                batch.color = Color.WHITE
                batch.draw(texture, 100f, 100f)
                batch.end()
            }
        )

        // -- PASS 2: Apply a grayscale post-processing effect --
        frameGraph.addPass(
            name = "Grayscale Post-Process",
            setup = {
                reads(sceneColor) // This pass depends on the output of the "Scene Pass"
                writesTo(finalOutput, Gdx.graphics.width, Gdx.graphics.height)
            },
            execute = { resources ->
                // Clear the screen with a black background
                Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

                batch.projectionMatrix = viewport.camera.combined
                batch.shader = grayscaleShader
                batch.begin()

                // Get the texture from the scene pass and draw it full-screen
                val sceneTexture = resources.getTexture(sceneColor)
                sceneTexture?.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
                batch.draw(sceneTexture, 0f, 0f, viewport.worldWidth, viewport.worldHeight, 0f, 0f, 1f, 1f)

                batch.end()
                batch.shader = null // Reset to default shader
            }
        )

        // 2. COMPILE THE GRAPH
        frameGraph.compile()
    }

    override fun render() {
        // 3. EXECUTE THE GRAPH
        frameGraph.execute()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)

        // In a real application, you would need to re-compile the graph
        // or handle resizing resources more robustly.
    }

    override fun dispose() {
        frameGraph.dispose()
        batch.dispose()
        texture.dispose()
        grayscaleShader.dispose()
    }

    private val vertexShader = """
        attribute vec4 ${ShaderProgram.POSITION_ATTRIBUTE};
        attribute vec2 ${ShaderProgram.TEXCOORD_ATTRIBUTE}0;
        uniform mat4 u_projTrans;
        varying vec2 v_texCoords;
        void main() {
            v_texCoords = ${ShaderProgram.TEXCOORD_ATTRIBUTE}0;
            gl_Position = u_projTrans * ${ShaderProgram.POSITION_ATTRIBUTE};
        }
    """.trimIndent()

    private val fragmentShader = """
        #ifdef GL_ES
            precision mediump float;
        #endif
        varying vec2 v_texCoords;
        uniform sampler2D u_texture;
        void main() {
            vec3 color = texture2D(u_texture, v_texCoords).rgb;
            float gray = dot(color, vec3(0.299, 0.587, 0.114));
            gl_FragColor = vec4(gray, gray, gray, 1.0);
        }
    """.trimIndent()
}
