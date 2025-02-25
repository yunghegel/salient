import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import dev.lyze.gdxtinyvg.utils.WhitePixelUtils
import org.yunghegel.salient.engine.ui.Popup
import org.yunghegel.salient.engine.ui.layout.TimeSeriesGraphActor
import org.yunghegel.salient.engine.ui.popup
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.widgets.EditableTextField
import space.earlygrey.shapedrawer.JoinType
import space.earlygrey.shapedrawer.ShapeDrawer

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

    @Test
    fun `test timeseries graph actor`() {
        actorTest(
            actor = {
                val pixel = WhitePixelUtils.createWhitePixelTexture()
                val batch: PolygonSpriteBatch = PolygonSpriteBatch()
                val drawer = ShapeDrawer(batch, pixel)
                TimeSeriesGraphActor(drawer).apply {
                    setValueProvider { Gdx.graphics.deltaTime }
                    setUpdateInterval(0.1f)
                }
            },
            configureActor = { actor ->
                actor as TimeSeriesGraphActor
                actor.widthFunction = { actor.width }
                actor.heightFunction = { actor.height }
                actor.setTimeWindow(10f)
                actor.setJoinType(JoinType.SMOOTH)
                actor.setSamples(50)
                actor.debugAll()


            }
        )
    }

}