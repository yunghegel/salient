package lwjgui

import lwjgui.paint.Color
import lwjgui.scene.Scene
import lwjgui.scene.Window
import lwjgui.scene.layout.StackPane
import lwjgui.style.BoxShadow


class BoxShadowTest : LWJGUIApplication() {

    fun run(args:Array<String>) {
        launch(args)
    }

    override fun start(args: Array<String>, window: Window) {
        // Create a simple root pane
        val pane = StackPane()


        // Create yellow pane
        val t = StackPane()
        t.setBackgroundLegacy(Color.YELLOW)
        t.setPrefSize(200.0, 100.0)
        t.setBorderRadii(3f)
        pane.children.add(t)


        // Add drop shadow
        t.boxShadowList.add(BoxShadow(20f, 20f, 50f, Color.GRAY))


        // Create a new scene
        window.scene = Scene(pane, WIDTH.toDouble(), HEIGHT.toDouble())


        // Make window visible
        window.show()
    }

    companion object {
        const val WIDTH: Int = 320
        const val HEIGHT: Int = 240

        @JvmStatic
        fun main(args: Array<String>) {
            BoxShadowTest().run(args)
        }
    }
}