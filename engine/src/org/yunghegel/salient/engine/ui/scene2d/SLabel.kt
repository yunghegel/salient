package org.yunghegel.salient.engine.ui.scene2d

import com.badlogic.gdx.scenes.scene2d.ui.Label
import ktx.async.schedule
import org.yunghegel.gdx.utils.temporal
import org.yunghegel.salient.engine.ui.UI
open class SLabel(text: String, styleName: String = "default") : Label(text, UI.skin, styleName) {

    private var provider: (() -> String)? = null

    var widthFunction : (() -> Float)? = null

    constructor(text: String, styleName: String = "default", provider: () -> String) : this(text, styleName) {
        this.provider = provider
    }

    val updateText = temporal(1f / 60f, true) {
        if (provider != null) {
            setText(provider!!.invoke())
        }
    }


    override fun act(delta: Float) {
        updateText.update(delta)
        super.act(delta)

    }

    override fun invalidate() {
        super.invalidate()

    }

    override fun getPrefWidth(): Float {
        return widthFunction?.invoke() ?: super.getPrefWidth()
    }

//    [#xxxxxxxx] Sets the color specified by the hex value xxxxxxxx in the form RRGGBBAA where AA is optional and defaults to 0xFF.
//      [] Sets the color to the previous color (kind of optional end tag)
//    f
    companion object {
    fun replaceAnsiWithMarkup(text: String): String {

        val sb = StringBuilder()
        var i = 0
        while (i < text.length) {
            if (text[i] == '[') {
                if (text[i + 1] == '#') {
                    val hex = text.substring(i + 2, i + 10)
                    val color = hex.toInt(16)
                    sb.append("[#${color.toString(16)}]")
                    i += 10
                } else {
                    sb.append("[#ffffff]")
                    i += 1
                }
            } else {
                sb.append(text[i])
                i += 1
            }
        }
        return sb.toString()
    }
    }

}
