import com.badlogic.gdx.Gdx
import org.yunghegel.salient.engine.ui.Popup
import org.yunghegel.salient.engine.ui.popup
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.widgets.EditableTextField

import types.actorTest
import kotlin.test.Test

class ActorTests {

    @Test
    fun `test actor creation`() {
        actorTest(
           actor = { EditableTextField("Test", { input -> println("$input : editing done")}) }
        )
    }

    @Test
    fun `test notification actor`() {
        actorTest(
           actor = {
               debug("input", { Gdx.input.inputProcessor::class.simpleName.toString() })
               stage.popup {
                   title("Test Notification")
                   content {
                       textInput("Enter your name", "salient user") { input -> result["name"] = input }
                   }
                   closeButton { println("Closed") }



                   submit("New Popup") { res ->
                       stage.popup {
                           title("New Popup")
                           content {
                               label("Hello ${res["name"]}")
                           }
                       }
                   }

               }
           }
        )
    }

}