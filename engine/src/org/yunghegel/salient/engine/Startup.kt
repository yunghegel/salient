package org.yunghegel.salient.engine

import com.badlogic.gdx.Gdx
import com.twelvemonkeys.lang.DateUtil
import gln.gl.timestamp
import org.apache.commons.lang3.time.DateUtils
import org.yunghegel.gdx.utils.State
import org.yunghegel.gdx.utils.StateMachine
import org.yunghegel.gdx.utils.ext.inc
import org.yunghegel.gdx.utils.ext.ref
import org.yunghegel.gdx.utils.ext.watch
import org.yunghegel.salient.engine.events.Bus
import org.yunghegel.salient.engine.events.Event
import org.yunghegel.salient.engine.system.profile
import java.time.Instant
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


enum class Phase : State<Phase>  {

    _STARTUP {
        

        override fun transition(to: Phase) {
            println("Transitioning from $this to $to")
        }
    },
    _LOAD_SETTINGS{
        

        override fun transition(to: Phase) {
            println("Transitioning from $this to $to")
        }
    },
    _VALIDATE_DIRECTORIES {
        

        override fun transition(to: Phase) {
            println("Transitioning from $this to $to")
        }
    },
    _DISCOVER_PROJECTS {
        

        override fun transition(to: Phase) {
            println("Transitioning from $this to $to")
        }
    },
    _LOAD_PROJECT {
        

        override fun transition(to: Phase) {
            println("Transitioning from $this to $to")
        }
    },
    _PROJECT_ASSET_INDEX_DISCOVERY {
        

        override fun transition(to: Phase) {
            println("Transitioning from $this to $to")
        }
    },
    _PROJECT_SCENE_INDEX_DISCOVERY {
        

        override fun transition(to: Phase) {
            println("Transitioning from $this to $to")
        }
    },
    _INITIALIZE_PROJECT {
        

        override fun transition(to: Phase) {
            println("Transitioning from $this to $to")
        }
    },
    _DISCOVER_SCENE {
        

        override fun transition(to: Phase) {
            println("Transitioning from $this to $to")
        }
    },
    _LOAD_SCENE {
        

        override fun transition(to: Phase) {
            println("Transitioning from $this to $to")
        }
    },
    _SCENE_ASSET_INDEX_DISCOVERY {
        

        override fun transition(to: Phase) {
            println("Transitioning from $this to $to")
        }
    },
    _LOAD_SCENE_ASSETS  {
        

        override fun transition(to: Phase) {
            println("Transitioning from $this to $to")
        }
    },
    _BUILD_SCENE_GRAPH {
        

        override fun transition(to: Phase) {
            println("Transitioning from $this to $to")
        }
    },
    _INITIALIZE_SCENE {
        

        override fun transition(to: Phase) {
            println("Transitioning from $this to $to")
        }
    },
    _LOAD_PLUGINS_AND_SYSTEMS {
        

        override fun transition(to: Phase) {
            println("Transitioning from $this to $to")
        }
    },
    _STARTUP_COMPLETE {
        

        override fun transition(to: Phase) {
            println("Transitioning from $this to $to")
        }
    };

    override val state: Phase
        get() = this
    override val type: Class<Phase>
        get() = Phase::class.java

    operator fun getValue(nothing: Nothing?, property: KProperty<*>): Phase {
        return this
    }

    override fun toString(): String {
        return name.replace("_"," ").trim().lowercase()
    }

    override fun update(delta: Float): Boolean {
        action?.let { return profile<Boolean>("Phase $descr",true) {
            it(delta)
        } }
        return true
    }
    
    var action : ((Float) -> Boolean)? = null

    var ifFalse : ((Float) -> Boolean)? = null


    val descr : String
        get() = name.replace("_"," ").lowercase()

    @PublishedApi()
    internal companion object {
        
       
    }

}





@Suppress()

val STARTUP = Phase._STARTUP
val LOAD_SETTINGS = Phase._LOAD_SETTINGS
val VALIDATE_DIRECTORIES = Phase._VALIDATE_DIRECTORIES
val DISCOVER_PROJECTS = Phase._DISCOVER_PROJECTS
val LOAD_PROJECT = Phase._LOAD_PROJECT
val PROJECT_ASSET_INDEX_DISCOVERY = Phase._PROJECT_ASSET_INDEX_DISCOVERY
val PROJECT_SCENE_INDEX_DISCOVERY = Phase._PROJECT_SCENE_INDEX_DISCOVERY
val INITIALIZE_PROJECT = Phase._INITIALIZE_PROJECT
val DISCOVER_SCENE = Phase._DISCOVER_SCENE
val LOAD_SCENE = Phase._LOAD_SCENE
val SCENE_ASSET_INDEX_DISCOVERY = Phase._SCENE_ASSET_INDEX_DISCOVERY
val LOAD_SCENE_ASSETS = Phase._LOAD_SCENE_ASSETS
val BUILD_SCENE_GRAPH = Phase._BUILD_SCENE_GRAPH
val INITIALIZE_SCENE = Phase._INITIALIZE_SCENE
val LOAD_PLUGINS_AND_SYSTEMS = Phase._LOAD_PLUGINS_AND_SYSTEMS
val STARTUP_COMPLETE = Phase._STARTUP_COMPLETE






var state : Phase by Startup

infix fun Phase.action(action: (Float) -> Boolean) : Phase {
    return this.also {
        this.action = action
    }
}

infix fun Phase.ifFalse(action: (Float) -> Boolean) : Phase {
    return this.also {
        this.ifFalse = action
    }
}

infix fun Phase.doing(action: (Float) -> Unit) : Phase {
    return this.also {
        this.action = { action(it); true }
    }
}

data class StateSetAction(val state: Phase, val action: () -> Unit)


object Startup : StateMachine<Phase>(Phase._STARTUP), ReadWriteProperty<Any?, Phase> {



    override var state: Phase by ref(Phase._STARTUP)

    data class StateMetaData(val phase: Phase) {
        val time = Instant.now()
        override fun toString(): String {
            return "$phase @ $time"
        }
    }


    init {
        watch(::state) {
            Bus.post(PhaseChangedEvent(it))
        }



    }

    operator fun inc() : Startup {
        this.state = Phase.entries[this.state.ordinal + 1]
        return this
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Phase {
        return state
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Phase) {
        state.update(Gdx.graphics.deltaTime)
        state.transition(value)
        state = value
    }


}
class StartupTracker : ReadWriteProperty<Any?, Phase> {

    var phase : Phase by ref(Phase._STARTUP)



    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Phase) {
        phase = value
    }






    private val listeners = mutableMapOf<Phase, MutableList<() -> Unit>>()

    fun on(phase: Phase, action: () -> Unit) {
        listeners.getOrPut(phase) { mutableListOf() }.add(action)
    }

    fun run(phase: Phase) {
        listeners[phase]?.forEach { it() }
    }

    interface Listener {
        fun onStartupPhaseChanged(phase: Phase)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Phase {
        return phase
    }


}

class PhaseChangedEvent(val phase: Phase) : Event() {

        interface Listener {
            fun onPhaseChanged(event: PhaseChangedEvent)
        }

    override fun toString(): String {
        return phase.name
    }
}

fun onStartupPhaseChanged(action: (Phase) -> Unit) = object : PhaseChangedEvent.Listener {

    init {
        Bus.register(this)
    }

    override fun onPhaseChanged(event: PhaseChangedEvent) {
        action(event.phase)
    }

}