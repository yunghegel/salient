package org.yunghegel.salient.editor.modules

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.PolygonBatch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.GLFormat
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import org.json.XMLTokener.entity
import org.yunghegel.salient.editor.plugins.gizmos.GizmoPlugin
import org.yunghegel.salient.editor.plugins.outline.OutlinerPlugin

import org.yunghegel.salient.editor.plugins.rendering.State
import org.yunghegel.salient.editor.render.RenderingPlugin

import org.yunghegel.salient.engine.GraphicsModule
import org.yunghegel.salient.engine.api.ecs.ObjectEntity
import org.yunghegel.salient.engine.api.plugin.Plugin
import org.yunghegel.salient.engine.graphics.FBO
import org.yunghegel.salient.engine.graphics.GFX
import org.yunghegel.salient.engine.graphics.GraphicsContext
import org.yunghegel.salient.engine.graphics.SharedGraphicsResources
import org.yunghegel.salient.engine.graphics.shapes.utility.Grid
import org.yunghegel.salient.engine.graphics.util.DebugDrawer
import org.yunghegel.salient.engine.helpers.Pools
import org.yunghegel.salient.engine.system.InjectionContext
import org.yunghegel.salient.engine.system.InjectionContext.bind
import org.yunghegel.salient.engine.system.inject
import space.earlygrey.shapedrawer.ShapeDrawer

import kotlin.collections.forEach
import kotlin.jvm.java
import kotlin.reflect.KClass

var state = State.INIT
class AutoremoveEntiy : ObjectEntity()

/**
 * A component that holds a function object, to be executed by a system.
 */

class FunctionComponent(val func:(Float) -> Unit) : SceneRenderEvent {
    context(SharedGraphicsResources)
    override fun render(delta: Float) {
        func(delta)
    }
}
fun interface SceneRenderEvent : Component {
    context(SharedGraphicsResources) fun render(delta:Float)
}
@JvmInline
value class TransitionComponent(val transition: () -> Unit) : Component

/**
 * A component that holds a condition that is checked every frame. If the condition is true, the entity is removed from the engine
 */
class AutoremoveConditionComponent(val condition: () -> Boolean) : Component

/**
 * A reference to the state we're interested in.
 *
 * Comes with an optional transitional function can be added that is called only when
 * the next state is reached, to use as middleware between states for things like profiling,
 * logging
 */
class StateComponent(val state: State, val transition: (() -> Unit)? = null) : Component


val buffers : MutableMap<String,FrameBuffer?> = mutableMapOf()


/**
 * Entity component system engine instance that is exclusive to the main rendering process. Maybe a faux-pas to have multiple engines
 * in a single application; never heard of this but it makes things easier to manage
 *
 * The final state is the UI pass which restores the appropriate OpenGL state for 2D rendering, so it's
 * appropriate to render more things after this stage if needed (dialogs, notifications, etc)
 */
open class GFXModule() : GraphicsModule(), GraphicsContext by GFX {

    override val plugins: MutableList<KClass<out Plugin>> = mutableListOf(OutlinerPlugin::class, GizmoPlugin::class,
        RenderingPlugin::class)
    override val registry: InjectionContext.() -> Unit = GFX.bindvalues
    init {
        bind<GraphicsModule> { this }
        bind<GFXModule> { this  }
    }



    /**
     * Provided a state, we create an entity that holds a function to be executed in that system
     * Importantly, we can use this function at any moment to "inject" a render call that depends
     * on the state of the OpenGL context at that moment.
     *
     *  For example, we may want to display bitmap text when the embedded 3D viewport state is applied. Calling it in place
     *  would be a bad idea since the OpenGL context is not set up for fullscreen 2D rendering at that moment (resulting in stretching or artifacts)
     *
     *  An in place modification to the state is possible instead, but this is more flexible and allows for more complex rendering operations later.
     *  Modifying the viewport like this might also cause graphical artifacts when resizing the window, so its better to specify when certain rendering actions are permitted
     *  This is useful for deferred rendering as well, we can demarcate a slice of the pipeline and buffer it for later
     *
     *  We define an initial set of operations that occur at each state that represent a configuration of some necessary set of the OpenGL state.
     *  Then, we access it later, and render things that depend on that state.
     *
     */
    fun push(state: State, transition: (() -> Unit)? = null, autoadd: Boolean = true, func: (Float) -> Unit) : Entity {
        val engine = inject<Engine>()
        val entity = Entity()
        if (transition != null) {
            val transitionComponent = TransitionComponent(transition)
            entity.add(transitionComponent)
        }
        entity.add(FunctionComponent(func))
        entity.add(state)
        if (autoadd) engine.addEntity(entity)
        return entity
    }

    fun reset() {
        val engine = inject<Engine>()

        engine.entities.forEach { entity ->
            engine.removeEntity(entity)
        }
    }

    override val priority: Int = 1

    fun push (entity:Entity) {
        val engine = inject<Engine>()

        require (entity.getComponent(FunctionComponent::class.java) != null && entity.getComponent(State::class.java) !=null) { "Entity must have FunctionComponent and StateComponent" }
        engine.addEntity(entity)
    }

    fun pull(entity:Entity) {
        val engine = inject<Engine>()

        engine.removeEntity(entity)
    }

    fun once(state: State, transition: (() -> Unit)? = null, func: (Float) -> Unit, ) {
        val engine = inject<Engine>()

        val entity = Entity()
        entity.add(FunctionComponent(func))
        if (transition != null) {
            val transitionComponent = TransitionComponent(transition)
            entity.add(transitionComponent)
        }
        entity.add(state)
        engine.addEntity(entity)
    }


    fun createRoutine(state: State, name:String, condition: (()->Boolean)? = null, func: (Float) -> Unit) : Entity {
        val entity = Entity()
        val buf = buildBuffer(name)
        buffers[name] = buf
        if (condition !=  null) {
            entity.add(AutoremoveConditionComponent(condition))
        }
        entity.add(FunctionComponent(func))
        entity.add(state)
        return entity
    }

    /**
     * Middleware utility. Example usage would be to profile the time taken to render a certain state, or log the number of texture bindings that occured during a state
     */

    @OptIn(ExperimentalStdlibApi::class)
    fun each(transition: (() -> Unit)? = null, func: (Float) -> Unit ) {
        for (state in State.entries) {
            push(state, transition, true, func)
        }
    }

    fun buildBuffer(named:String, depth: Boolean = true, multisample: Boolean = false, width: Int = Gdx.graphics.backBufferWidth, height: Int = Gdx.graphics.backBufferHeight ) : FrameBuffer {
        val buf = if (multisample) {
            FBO.createMultisample(GLFormat.RGBA32, width,height, depth, 4)
        } else {
            FBO.create(GLFormat.RGBA32, width,height, depth)
        }
        buffers[named] = buf
        return buf
    }

    fun update(deltaTime: Float) {
    }




}





