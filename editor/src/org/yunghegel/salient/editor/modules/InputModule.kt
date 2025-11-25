package org.yunghegel.salient.editor.modules

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.utils.SnapshotArray
import ktx.collections.removeAll
import org.yunghegel.gdx.utils.ext.computed
import org.yunghegel.gdx.utils.ext.ref
import org.yunghegel.gdx.utils.ext.toGdxArray
import org.yunghegel.gdx.utils.ext.watch
import org.yunghegel.salient.editor.plugins.intersect.IntersectionPlugin
import org.yunghegel.salient.editor.plugins.picking.PickingPlugin
import org.yunghegel.salient.engine.InputModule
import org.yunghegel.salient.engine.api.plugin.Plugin
import org.yunghegel.salient.engine.system.InjectionContext
import org.yunghegel.salient.engine.system.InjectionContext.bind
import org.yunghegel.salient.engine.system.Netgraph
import kotlin.reflect.KClass

class InputModule : InputModule() {
    override val priority: Int = 2
    private var paused = false
    var button = -1
    var key = -1
    var tmp : SnapshotArray<InputProcessor> = SnapshotArray()

    init {

        inputProcessor = this
        Netgraph.add("Multiplexer: ") {
            val sb = StringBuilder()
            sb.append("Multiplexer: ")
            for (i in 0 until processors.size) {
                sb.append(processors[i].javaClass.simpleName)
                if (i < processors.size - 1) {
                    sb.append(", ")
                }
            }
            sb.toString()
        }
        bind<Input> { this }
    }


    override val registry: InjectionContext.() -> Unit = {
        bindSingleton(InputMultiplexer::class, InputAdapter::class) { this }
    }
    override val plugins: MutableList<KClass<out Plugin>> = mutableListOf( IntersectionPlugin::class)

    override fun pauseExcept(vararg processor: InputProcessor) {
        paused = true

        tmp.clear()
        tmp.addAll(processor.toGdxArray())
        tmp.addAll(processors)

        processors.clear()
        processors.addAll(processor.toGdxArray())
    }

    override fun resumeExcept(vararg processor: InputProcessor) {
        paused = false

        tmp.clear()
        tmp.addAll(processors)
        tmp.removeAll(processor,true)

        processors.clear()
        processors.addAll(tmp)
    }

    override fun pause() {
        paused = true
        tmp.clear()
        tmp.addAll(processors)
        processors.clear()
    }

    override fun resume() {
        paused = false
        processors.clear()
        processors.addAll(tmp)
    }

    companion object {
//        live tracking of input state
        var keyDown by ref(Gdx.input.isTouched)

        init {
            computed {
                println("Key Down: $keyDown")
            }
        }


    }


}