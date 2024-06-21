package org.yunghegel.salient.engine.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter
import ktx.actors.onChange
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.salient.engine.ui.label
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.scene2d.STextField
import org.yunghegel.salient.engine.ui.widgets.value.widgets.FloatOnlyFilter

class FloatField(name:String,val negative: Boolean = true,val accessor: ()->Float, val setter: (Float)->Unit) : STable() {

    var lastVal = 0f

    val display = label(accessor.toString())
    init {
        add(SLabel(name)).padHorizontal(4f)
        val editor = STextField(accessor().toString()).apply {
            textFieldFilter = FloatOnlyFilter(negative)
            onChange {
                val res = safeStringToFloat(this.text)
                setter(res)
                display.setText(res.toString())
            }
        }
        add(editor).maxWidth(60f)

    }

    fun safeStringToFloat(value :String) : Float {
        var tmp = 0f
        try {
            tmp =  value.toFloat()
        } catch (e: Exception) {
            println(e.message)
        } finally {
            lastVal = tmp
        }
        return tmp

    }


}