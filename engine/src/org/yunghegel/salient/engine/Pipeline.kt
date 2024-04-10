package org.yunghegel.salient.engine

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem

class UILogicSystem() : StateSystem(State.UI_LOGIC)
class InitStateSystem() : StateSystem(State.INIT)
class PrepareSceneSystem() : StateSystem(State.PREPARE_SCENE)
class BeforeDepthPassSystem() : StateSystem(State.BEFORE_DEPTH_PASS)
class DepthPassSystem() : StateSystem(State.DEPTH_PASS)
class BeforeColorPassSystem() : StateSystem(State.BEFORE_COLOR_PASS)
class ColorPassSystem() : StateSystem(State.COLOR_PASS)
class UIPassSystem() : StateSystem(State.UI_PASS)

/**
 * Enumeration of necessary render states that must occur in a specific order; at particular moments we have to access the OpenGL context
 * and modify it to support the rendering of things in a certain order
 */
enum class State() {
    INIT(),
    UI_LOGIC,
    PREPARE_SCENE,
    BEFORE_DEPTH_PASS,
    DEPTH_PASS,
    BEFORE_COLOR_PASS,
    COLOR_PASS,
    UI_PASS;
}

/**
 * ECS-powered iterating system that is dedicated to handling a specific state in the rendering pipeline.
 * Functions are carried in by components, owned by an entity which also references a state. The function
 * is executed when the entity's state matches the system's state.
 *
 * The function is executed in the same order every time since we map the enum ordinal to the int priority of the system.
 *  i.e, the enum ordinal is an index into the array of systems
 */
sealed class StateSystem(val state: State) : IteratingSystem(Family.all(FunctionComponent::class.java, StateComponent::class.java).get(), state.ordinal) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        entity?.let {
            val stateCmp = it.getComponent(StateComponent::class.java)

            if(stateCmp.state != state) return

            val func = it.getComponent(FunctionComponent::class.java).func

            func(deltaTime)
            stateCmp.transition?.let { transition -> transition() }
        }

        if (entity is AutoremoveEntiy) {
            engine.removeEntity(entity)
        }
    }
}

class AutoremoveEntiy : Entity()

/**
 * A component that holds a function object, to be executed by a system.
 */
class FunctionComponent(val func: (Float) -> Unit) : Component

/**
 * A reference to the state we're interested in.
 *
 * Comes with an optional transitional function can be added that is called only when
 * the next state is reached, to use as middleware between states for things like profiling,
 * logging
 */
class StateComponent(val state: State, val transition: (() -> Unit)? = null) : Component


/**
 * Entity component system engine instance that is exclusive to the main rendering process. Maybe a faux-pas to have multiple engines
 * in a single application; never heard of this but it makes things easier to manage
 *
 * The final state is the UI pass which restores the appropriate OpenGL state for 2D rendering, so it's
 * appropriate to render more things after this stage if needed (dialogs, notifications, etc)
 */
class Pipeline : Engine() {

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
        UIPassSystem()
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
    fun push(state: State, transition: (() -> Unit)? = null, func: (Float) -> Unit) {
        val entity = Entity()
        entity.add(FunctionComponent(func))
        entity.add(StateComponent(state, transition))
        addEntity(entity)
    }

    fun once(state: State, func: (Float) -> Unit, transition: (() -> Unit)? = null) {
        val entity = AutoremoveEntiy()
        entity.add(FunctionComponent(func))
        entity.add(StateComponent(state, transition))
        addEntity(entity)
    }

    /**
     * Middleware utility. Example usage would be to profile the time taken to render a certain state, or log the number of texture bindings that occured during a state
     */

    fun each(func: (Float) -> Unit, transition: (() -> Unit)? = null) {
        for (state in State.entries) {
            push(state, transition, func)
        }
    }

}





