package org.yunghegel.salient.engine.api.tool

import com.badlogic.gdx.math.Vector2
import org.yunghegel.salient.engine.input.Input


abstract class ClickTool(name: String) : MouseTool(name) {

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == Input.Buttons.LEFT) {
            create(snap(unproject(out,screenX.toFloat(), screenY.toFloat())))
            return true
        } else if (button == Input.Buttons.RIGHT) {
            end()
            return true
        }
        return super.touchDown(screenX, screenY, pointer, button)
    }

    /**
     *
     * @param position coordinates unprojected from click in the 2D grid coordinates system (+X,+Y), potentianally snaped to the 2D grid.
     * @todo snapping shouldn't be done here but the snap process should be injected in some way.
     */
    protected abstract fun create(position: Vector2?)
}