package org.yunghegel.salient.editor.render

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import org.yunghegel.gdx.utils.ext.clearColor
import org.yunghegel.gdx.utils.ext.clearDepth
import org.yunghegel.gdx.utils.ext.getBounds
import org.yunghegel.gdx.utils.ext.toOpenGLCoords
import org.yunghegel.gdx.utils.ext.topRight
import org.yunghegel.salient.editor.app.Salient
import org.yunghegel.salient.editor.modules.ui
import org.yunghegel.salient.editor.render.lib.FrameBuffers
import org.yunghegel.salient.editor.render.systems.RenderingSystem
import org.yunghegel.salient.engine.api.ecs.System
import org.yunghegel.salient.engine.api.plugin.Plugin
import org.yunghegel.salient.engine.api.tool.Tool
import org.yunghegel.salient.engine.graphics.GFX.modelBatch
import org.yunghegel.salient.engine.graphics.GFX.perspectiveCamera
import org.yunghegel.salient.engine.system.InjectionContext
import org.yunghegel.salient.engine.ui.UI

class RenderingPlugin : Plugin {

    val renderingSystem = RenderingSystem()
    val framebuffers: FrameBuffers = FrameBuffers()

    override val systems: MutableList<System<*, *>> = mutableListOf(renderingSystem)

    override val tools: MutableList<Tool> = mutableListOf(framebuffers)

    override val registry: InjectionContext.() -> Unit = {
        singleton(this)
        singleton(framebuffers)
        singleton(renderingSystem)
    }

    override fun init(engine: Engine) {
        val context = engine as Salient
        with(context.scene.context) {
            renderingSystem.addRenderFunction(11) { delta ->
                clearColor()
                clearDepth()
            }


            renderingSystem.addRenderFunction(10) { delta ->

                with(context) {
                    ui.act(delta)
                    gui.viewportWidget.update()

                }
            }
            renderingSystem.addRenderFunction(9) { delta ->
                with(context) {
                    scene.update(delta)
                    viewportController.update()
                    scene.renderer.prepareContext(scene.context, true)
                }
            }

            renderingSystem.addRenderFunction(8) { delta ->
                with(context) {
                scene.renderer.prepareContext(scene.context, true)
                scene.renderer.renderContext(scene.context)
                scene.renderer.prepareContext(scene.context, false)

                }
            }
            renderingSystem.addRenderFunction(7) { delta ->
                with(context) {
                    gui.updateviewport()
                    modelBatch.begin(perspectiveCamera)
                    scene.graph.root.renderColor(delta, modelBatch, scene.context)
                    scene.graph.root.renderDebug(scene.context)
                    modelBatch.end()

                    with(gui.viewportWidget.compass) {
                        val pos = toOpenGLCoords(gui.viewportWidget.getBounds().topRight()).sub(0.06f, 0.065f)
                        setPos(0.94f, 0.935f)
                        update(Gdx.graphics.deltaTime, gui.viewportWidget.width, gui.viewportWidget.height)
                        render(delta)
                    }
                }
            }
            renderingSystem.addRenderFunction(6) { delta ->
                with(context) {
                    ui.viewport.apply()
                        ui.draw()
                        UI.DialogStage.apply {
                            act()
                            draw()
                        }
                }
            }

        }
    }

    override val name: String = "rendering_plugin"

}