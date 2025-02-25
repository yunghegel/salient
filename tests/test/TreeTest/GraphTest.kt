package TreeTest

import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import dev.lyze.gdxtinyvg.utils.WhitePixelUtils
import org.yunghegel.salient.engine.ui.layout.TimeSeriesGraphActor
import space.earlygrey.shapedrawer.JoinType
import space.earlygrey.shapedrawer.ShapeDrawer
import types.actorTest


fun main() {
    actorTest(
        actor =
        {
            val pixel = WhitePixelUtils.createWhitePixelTexture()
            val batch: PolygonSpriteBatch = PolygonSpriteBatch()
            val drawer = ShapeDrawer(batch, pixel)

            TimeSeriesGraphActor(drawer).apply {
                setValueProvider { 0.5f }
                setUpdateInterval(0.1f)
            }
        },
        configureActor = { actor ->
            actor as TimeSeriesGraphActor
            actor.setTimeWindow(10f)
            actor.setJoinType(JoinType.SMOOTH)
            actor.setSamples(50)

        }
    )
}