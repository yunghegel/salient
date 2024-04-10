package org.yunghegel.salient.engine.ui.scene2d

import com.badlogic.gdx.scenes.scene2d.ui.Label
import org.yunghegel.salient.engine.ui.UI
open class SLabel(text: String, styleName: String = "default") : Label(text, UI.skin, styleName) {

    private var provider: (() -> String)? = null

    constructor(text: String, styleName: String = "default", provider: () -> String) : this(text, styleName) {
        this.provider = provider
    }

    override fun act(delta: Float) {
        if (provider != null) {
            setText(provider!!.invoke())
        }
        super.act(delta)
    }

}
