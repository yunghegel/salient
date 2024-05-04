package org.yunghegel.salient.engine.ui.widgets

import com.badlogic.gdx.utils.Align
import ktx.actors.onChange
import ktx.scene2d.checkBox
import ktx.scene2d.scene2d
import org.yunghegel.gdx.utils.data.Mask
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.salient.engine.ui.scene2d.STable

class FlagsEditor (var mask : Mask? = null) : STable() {

    var children = 0

    init {
        pad(5f)
        mask?.let { set(it) }
        align(Align.left )
    }

    fun set(mask : Mask){
        this.mask = mask
        clear()
        mask.set.registry.forEach { (name, flag) ->
            addFlag(name, flag)
        }
    }

    fun addFlag(name : String, flag : Int){
        val checkBox = scene2d.checkBox(name)
        checkBox.isChecked = mask!!.has(flag)
        checkBox.onChange {

            if(mask!=null) {
                mask!!.toggle(flag)
            }
            mask?.forEach { name, value ->
                println("$name : $value")
            }

        }
        add(checkBox).growX().padHorizontal(5f).left()
        children++
        if (children%2==0) {
            row()
        }
    }



}