
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.widgets.EditableTextField
import org.yunghegel.salient.engine.ui.widgets.notif.AlertStrategy
import org.yunghegel.salient.engine.ui.widgets.notif.Popup
import org.yunghegel.salient.engine.ui.widgets.notif.Severity
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
                Popup("Test", "Test", SLabel("This is a test notification"))
           }
        )
    }

}