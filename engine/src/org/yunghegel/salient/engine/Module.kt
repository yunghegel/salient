package org.yunghegel.salient.engine

import com.badlogic.gdx.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import org.yunghegel.salient.engine.system.InjectionContext
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.system.injectUnsafe
import org.yunghegel.salient.engine.system.register

interface Module  {

    val afterEval : MutableList<()->Unit>

    val beforeEval : MutableList<()->Unit>

    val registry : InjectionContext.() -> Unit

    fun initialize() {
        beforeEval.forEach { it() }
        register {
            registry()
        }
        afterEval.forEach { it() }
    }

}

abstract class GraphicsModule : Module, Graphics by Gdx.graphics {
    override val afterEval: MutableList<() -> Unit> = mutableListOf()
    override val beforeEval: MutableList<() -> Unit> = mutableListOf()
}
abstract class InputModule : InputMultiplexer(), Module, Input by Gdx.input {
    override val afterEval: MutableList<() -> Unit> = mutableListOf()
    override val beforeEval: MutableList<() -> Unit> = mutableListOf()
}
abstract class UIModule : Stage(ScreenViewport(), inject<SpriteBatch>()), Module {
    override val afterEval: MutableList<() -> Unit> = mutableListOf()
    override val beforeEval: MutableList<() -> Unit> = mutableListOf()
}
abstract class IOModule : Module, Files by Gdx.files {
    override val afterEval: MutableList<() -> Unit> = mutableListOf()
    override val beforeEval: MutableList<() -> Unit> = mutableListOf()
}
abstract class AppModule : Module, Application by Gdx.app {
    override val afterEval: MutableList<() -> Unit> = mutableListOf()
    override val beforeEval: MutableList<() -> Unit> = mutableListOf()
}


inline fun <reified T: Module> mustRunAfter(noinline action: ()->Unit) {
    val module :T =  injectUnsafe()
    module.beforeEval.add(action)
}

inline fun <reified T: Module> mustRunBefore(noinline action: ()->Unit) {
    val module :T =  injectUnsafe()
    module.afterEval.add(action)
}