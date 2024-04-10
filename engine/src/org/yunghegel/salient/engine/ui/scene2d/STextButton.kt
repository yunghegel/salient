package org.yunghegel.gdx.utils.ui.scene2d

import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import ktx.actors.onChange
import org.yunghegel.salient.engine.ui.UI
class STextButton(text: String, styleName: String = "default") : TextButton(text, UI.skin, styleName) {
    constructor(text: String) : this(text, "default")
    constructor(text:String,onChange:()->Unit) : this(text) {
        onChange { onChange() }
    }
}


