package org.yunghegel.salient.engine.ui.widgets.notif

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.toast.Toast
import kotlinx.serialization.json.JsonNull.content
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.scene2d.SWindow
import org.yunghegel.salient.engine.ui.widgets.viewport.ViewportPanel

class Toast(val named: String, returns: Any, content: STable  ) : Toast(named,content) {

}