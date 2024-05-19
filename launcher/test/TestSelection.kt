import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import ktx.collections.toGdxArray
import org.yunghegel.salient.engine.ui.scene2d.SList
import org.yunghegel.salient.engine.ui.scene2d.SWindow
import types.Lwjgl3Test
import types.Test

class TestSelection(val game: TestApplication, val tests : List<Test>) : ScreenAdapter() {

    val list : SList<Test> = SList()
    val window = SWindow("Test Selection")
    var last : Test? = null
    init {
        list.setItems(tests.toGdxArray())
        window.add(list).grow()

        window.apply {
            isModal = true
            isMovable = false
            isResizable = false
            pack()
        }

        list.listener = SList.SListListener() { list, previousSelectedIndex, newSelectedIndex ->
            val selection = tests[newSelectedIndex]
            (selection as Lwjgl3Test).bundle.input.addProcessor(game.stage)

            last = tests[previousSelectedIndex]
            list.selectedIndex = -1
        }

        window.setCenterOnAdd(true )

        game.stage.addActor(window)

        game.screen = this
    }

    override fun hide() {

    }

    override fun render(delta: Float) {
        super.render(delta)
    }

    override fun show() {
        Gdx.input.inputProcessor = game.stage
    }

    override fun resume() {
        super.resume()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
    }
}