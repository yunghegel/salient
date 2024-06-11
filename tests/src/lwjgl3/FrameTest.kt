package lwjgl3

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.app.clearScreen
import org.yunghegel.gdx.utils.ext.clearColor
import org.yunghegel.gdx.utils.ext.clearDepth
import org.yunghegel.gdx.utils.ext.notnull
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.layout.EditorFrame
import org.yunghegel.salient.engine.ui.scene2d.STable

class FrameTest : ApplicationAdapter() {

    var stage: Stage by notnull()
    var root : EditorFrame by notnull()

    init {

    }

    override fun create() {
        UI.init()
        stage = Stage()
        Gdx.input.inputProcessor = stage
        root = EditorFrame()
        stage.addActor(root)
        root.setFillParent(true)

        root.addLeft("project", "Project", STable())
        root.addLeft("scene_icon", "Project", STable())

        root.addRight("scene_icon", "Scene", STable())
        root.addRight("project", "Project", STable())

        root.addCenter("terminal", "Terminal", STable())
        root.addCenter("terminal", "Log", STable())

    }

    override fun render() {
        clearColor()
        clearDepth()
        clearScreen(0.2f,0.2f,0.2f)
        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

}

