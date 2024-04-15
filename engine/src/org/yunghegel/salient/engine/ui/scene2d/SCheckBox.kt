package org.yunghegel.salient.engine.ui.scene2d

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import org.yunghegel.salient.engine.ui.UI

open class SCheckBox(label:String) : CheckBox(label, UI.skin) {

    constructor(label:String,checked:Boolean) : this(label) {
        isChecked = checked
    }

    constructor(label:String,checked:Boolean,listener: (Boolean) -> Unit) : this(label,checked) {
        addListener {
            listener(isChecked)
            true
        }
    }

}