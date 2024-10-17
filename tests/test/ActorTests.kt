
import org.yunghegel.salient.engine.ui.widgets.EditableTextField
import types.actorTest
import kotlin.test.Test

class ActorTests {

    @Test
    fun `test actor creation`() {
        actorTest(
            { EditableTextField("Test", { input -> println("$input : editing done")}) }
        )
    }

}