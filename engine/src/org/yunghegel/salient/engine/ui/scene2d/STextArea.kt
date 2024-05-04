package org.yunghegel.salient.engine.ui.scene2d

import com.kotcrab.vis.ui.widget.ScrollableTextArea

open class STextArea(text: String? = null, style : String = "textArea") : ScrollableTextArea(text,style) {

    fun sanitizeCarriageReturn(current: String?) {
        text = current?.replace("\r", "")
    }

    override fun setText(str: String?) {
        sanitizeCarriageReturn(str!!)
        super.setText(str)
    }

}