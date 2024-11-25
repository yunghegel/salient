package org.yunghegel.salient.engine

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.GLFormat
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer.FrameBufferBuilder
import net.mgsx.gltf.scene3d.scene.Scene
import org.yunghegel.salient.engine.api.properties.Resizable
import org.yunghegel.salient.engine.graphics.FBO
import org.yunghegel.salient.engine.graphics.GFX
import org.yunghegel.salient.engine.graphics.SharedGraphicsResources
import org.yunghegel.salient.engine.helpers.Pools

import org.yunghegel.salient.engine.ui.widgets.notif.notify

class UILogicSystem : StateSystem(State.UI_LOGIC)
class InitStateSystem : StateSystem(State.INIT)
class PrepareSceneSystem : StateSystem(State.PREPARE_SCENE)
class BeforeDepthPassSystem : StateSystem(State.BEFORE_DEPTH_PASS)
class DepthPassSystem : StateSystem(State.DEPTH_PASS)
class BeforeColorPassSystem : StateSystem(State.BEFORE_COLOR_PASS)
class ColorPassSystem : StateSystem(State.COLOR_PASS)
class AfterColorPassSystem : StateSystem(State.AFTER_COLOR_PASS)
class BeforeUIPassSystem : StateSystem(State.BEFORE_UI_PASS)
class UIPassSystem : StateSystem(State.UI_PASS)
class OverlayPassSystem : StateSystem(State.OVERLAY_PASS)


/**
 * Enumeration of necessary render states that must occur in a specific order; at particular moments we have to access the OpenGL context
 * and modify it to support the rendering of things in a certain order
 */
enum class State :Component {
    INIT(),
    UI_LOGIC,
    PREPARE_SCENE,
    BEFORE_DEPTH_PASS,
    DEPTH_PASS,
    BEFORE_COLOR_PASS,
    COLOR_PASS,
    AFTER_COLOR_PASS,
    BEFORE_UI_PASS,
    UI_PASS,
    OVERLAY_PASS;
}

/**
 * ECS-powered iterating system that is dedicated to handling a specific state in the rendering pipeline.
 * Functions are carried in by components, owned by an entity which also references a state. The function
 * is executed when the entity's state matches the system's state.
 *
 * The function is executed in the same order every time since we map the enum ordinal to the int priority of the system.
 *  i.e, the enum ordinal is an index into the array of systems
 */
sealed class StateSystem(val state: State) : IteratingSystem(Family.all(FunctionComponent::class.java, State::class.java).get(), state.ordinal) {

    val doFirsts : MutableList<()->Unit> = mutableListOf()
    val finalizedBy : MutableList<()->Unit> = mutableListOf()
    fun first(func: () -> Unit) {
        doFirsts.add(func)
    }

    fun finalize(func: () -> Unit) {
        finalizedBy.add(func)
    }
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        entity?.let { e ->
            val stateCmp = e.getComponent(State::class.java)

            if(stateCmp != state) return

            val func = e.getComponent(FunctionComponent::class.java).func

            func(deltaTime)

            e.getComponent(TransitionComponent::class.java)?.let { trans ->
                trans.transition()
            }

            val autoremoveCondition = e.getComponent(AutoremoveConditionComponent::class.java)
            if (autoremoveCondition != null && autoremoveCondition.condition()) {
                notify("Automatically removed render routine according to condition")
                engine.removeEntity(e)
                Pools.entityPool.free(e)
            }

        }

        if (entity is AutoremoveEntiy) {
            engine.removeEntity(entity)
            Pools.autotoremoveEntityPool.free(entity)
        }

    }

    override fun update(deltaTime: Float) {
        doFirsts.forEach { it() }
        super.update(deltaTime)
        finalizedBy.forEach { it() }

    }
}

class AutoremoveEntiy : Entity()

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

/**
 * A reference to the state we're interested in.
 *
 * Comes with an optional transitional function can be added that is called only when
 * the next state is reached, to use as middleware between states for things like profiling,
 * logging
 */
class StateComponent(val state: State, val transition: (() -> Unit)? = null) : Component

@JvmInline
value class TransitionComponent(val transition: () -> Unit) : Component

/**
 * A component that holds a condition that is checked every frame. If the condition is true, the entity is removed from the engine
 */

class AutoremoveConditionComponent(val condition: () -> Boolean) : Component


/**
 * Entity component system engine instance that is exclusive to the main rendering process. Maybe a faux-pas to have multiple engines
 * in a single application; never heard of this but it makes things easier to manage
 *
 * The final state is the UI pass which restores the appropriate OpenGL state for 2D rendering, so it's
 * appropriate to render more things after this stage if needed (dialogs, notifications, etc)
 */
open class Pipeline() : Engine() {

    var colorTex : Texture? = null
    var depthTex : Texture? = null


    val buffers : MutableMap<String,FrameBuffer?> = mutableMapOf()


    /**
     * Series of {@see #in  } are added to the engine - one for each state.
     * Each system is dedicated to handling OpenGL a certain set of OpenGL state mutations and render calls.
     * Executed in the same order every time since we map the enum ordinal to the int priority of the system.
     *  i.e, the enum ordinal is an index into the array of systems
     */

    val systems: Array<StateSystem> = arrayOf(
        InitStateSystem(),
        UILogicSystem(),
        PrepareSceneSystem(),
        BeforeDepthPassSystem(),
        DepthPassSystem(),
        BeforeColorPassSystem(),
        ColorPassSystem(),
        AfterColorPassSystem(),
        BeforeUIPassSystem(),
        UIPassSystem(),
        OverlayPassSystem()
    )

    /**
     * Initialization, maintains the strict ordering required
     */
    fun initSystems() {
        systems.forEach { system -> addSystem(system) }
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
    fun push(state: State, transition: (() -> Unit)? = null,autoadd: Boolean = true, func: (Float) -> Unit) : Entity {
        val entity = Entity()
        if (transition != null) {
            val transitionComponent = TransitionComponent(transition)
            entity.add(transitionComponent)
        }
        entity.add(FunctionComponent(func))
        entity.add(state)
        if (autoadd) addEntity(entity)
        return entity
    }

    fun reset() {
        entities.forEach { entity ->
            removeEntity(entity)
        }
    }

    fun push (entity:Entity) {
        require (entity.getComponent(FunctionComponent::class.java) != null && entity.getComponent(State::class.java) !=null) { "Entity must have FunctionComponent and StateComponent" }
        addEntity(entity)
    }

    fun pull(entity:Entity) {
        removeEntity(entity)
    }

    fun once(state: State, transition: (() -> Unit)? = null,func: (Float) -> Unit, ) {
        val entity = Pools.autotoremoveEntityPool.obtain()
        entity.add(FunctionComponent(func))
        if (transition != null) {
            val transitionComponent = TransitionComponent(transition)
            entity.add(transitionComponent)
        }
        entity.add(state)
        addEntity(entity)
    }


    fun createRoutine(state:State,name:String,condition: (()->Boolean)? = null, func: (Float) -> Unit) : Entity {
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

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
    }


}





