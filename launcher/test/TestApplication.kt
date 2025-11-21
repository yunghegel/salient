import com.badlogic.gdx.Game
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.google.common.reflect.ClassPath
import com.kotcrab.vis.ui.widget.Menu
import com.kotcrab.vis.ui.widget.MenuBar
import com.kotcrab.vis.ui.widget.MenuItem
import org.yunghegel.gdx.utils.ext.*
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.textbutton
import org.yunghegel.salient.engine.ui.widgets.viewport.ViewportWidget
import registry.KnownTests
import types.Lwjgl3Test
import types.Test
import lwjgl3.*

class TestApplication : Game() {

    var appBar : MenuBar by notnull()
    var stage : Stage by notnull()
    var root : STable by notnull()
    var appBarActors : STable by notnull()
    var titleLabel : SLabel by notnull()

    lateinit var testSelection : TestSelection
    lateinit var viewportWidget : ViewportWidget

    lateinit var viewport: Viewport

    var currentTest : Lwjgl3Test? = null

    lateinit var tests : List<Test>

    fun scanTests() {
        ClassPath.from(Thread.currentThread().contextClassLoader).getTopLevelClasses("tests").forEach { cls ->
            println(cls)
        }
    }

    override fun create() {
        UI.init()

        stage = Stage()
        root = STable()
        stage.addActor(root)
        root.setFillParent(true)

        appBar = MenuBar()
        val table = appBar.table
        titleLabel = SLabel("Salient Tests")
        val items  = table.removeActorAt(0, true)
        table.add(titleLabel).height(20f).align(Align.left).padHorizontal(10f)
        table.add(items).height(20f).align(Align.left).padHorizontal(3f)
        appBarActors = STable()
        table.add(appBarActors).growX().height(20f).align(Align.center).row()

        viewport = ScreenViewport()
        viewportWidget = ViewportWidget(viewport as ScreenViewport)

        initUI()
        scanTests()

        tests = listOf(_ArcBallTest, DepthBufferTest,AsyncModelLoadTest)

        testSelection = TestSelection(this, tests)
        setScreen(testSelection)
    }

    fun initUI() {
        root.add(appBar.table).growX().height(20f).row()
        val tests = Menu("Tests")
        appBar.addMenu(tests)
        KnownTests.forEach { name, test ->
            createMenuItem(tests, name) {
                titleLabel.setText(name)

            }
        }

        createMenuButton("Reset" ) {
            currentTest?.let { it.reset() }
        }

        root.add(viewportWidget).grow()
    }

    fun setTest(test: Test) {
        test.ifInstance(Lwjgl3Test::class) { t ->
            t.execCreate()


            titleLabel.setText(t.name)

            t.bundle.input.addProcessor(stage)
        }
    }

    fun createMenuItem(parent: Menu, name: String, block: ()->Unit) {
        val item = MenuItem(name)
        parent.addItem(item)
        item.addListener {
            block()
            true
        }
    }

    fun createMenuButton(name: String, block: ()->Unit) {
        val btn = textbutton(name) {
            block()
        }
        appBarActors.add(btn).height(20f).padHorizontal(3f)
    }

    override fun render() {
        clearColor()
        clearDepth()
        stage.act()
        currentTest?.let { test ->
            viewportWidget.updateViewport(false)
        }
        super.render()

        stage.viewport.apply()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width,height)
        super.resize(width, height)
    }
}