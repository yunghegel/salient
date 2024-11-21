package types

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import org.yunghegel.gdx.utils.ext.clearScreen
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.STable

class ActorTest(val first: ()->Unit = {}, val createActor: ()-> Actor,val configureActor : (Actor)->Unit = {}) : BaseTest("ActorTest") {

    lateinit var stage : Stage
    lateinit var table : STable
    lateinit var actor : Actor

    override var execCreate: () -> Unit = {
        UI.init()
        first()
        stage = Stage(ScreenViewport())
        table = STable()
        table.setFillParent(true)
        table.align(Align.center)
        actor = createActor()
        stage.addActor(table)

        configureActor(actor)
        table.add(actor).center()

        Gdx.input.inputProcessor = stage
    }

    override var execRender: () -> Unit = {
        clearScreen(0.2f,0.2f,0.2f,1f)
        stage.act()
        stage.draw()
    }

    override fun create() {
        execCreate()
    }

    override fun render() {
        execRender()
    }

    override fun dispose() {
        stage.dispose()
    }
}

fun actorTest(first: ()->Unit = {}, actor: ()->Actor, configureActor : (Actor)->Unit = {}) {
    val config = Lwjgl3ApplicationConfiguration().apply {
        setTitle("ActorTest")
        setWindowedMode(400,400)
    }
    Lwjgl3Application(
    ActorTest(first, actor,configureActor),config)
}
