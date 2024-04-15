package org.yunghegel.salient.engine.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import ktx.actors.onChange
import ktx.actors.onClick
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.scene2d.STextField

class IconTextfield(option:Option = Option.LEFT, icon: String,text:String )  :STable() {

    enum class Option {
        LEFT,RIGHT,BOTH
    }

    val style = IconTextfieldStyle(icon, textfieldStyle = "default")

    val leftIcon = if(option == Option.LEFT || option == Option.BOTH) SImageButton(style.leftButtonStyle) else null
    val rightIcon = if(option == Option.RIGHT || option == Option.BOTH) SImageButton(style.rightButtonStyle) else null

    val leftTable = STable()
    val rightTable = STable()
    val textField = STextField(text,style.textfield_style)

    var listener: IconTextfieldListener? = null
        private set

    interface IconTextfieldListener {
        fun onLeftIconClick()
        fun onRightIconClick()
        fun onTextChange(text: String)
    }

    fun cancelFocus() {
        textField.cancelFocus()
    }

    fun setListener(listener: IconTextfieldListener) {
        this.listener = listener

        leftIcon?.onClick {
            listener.onLeftIconClick()
        }
        rightIcon?.onClick {
            listener.onRightIconClick()
        }
        textField.onChange {
            listener.onTextChange(textField.text)
        }

    }

    init {
        leftTable.background = style.leftIconBackground
        rightTable.background = style.rightIconBackground
        leftIcon?.let { leftTable.add(it) }
        rightIcon?.let { rightTable.add(it)}

        val container = STable()
        with(container) {
            if (option == Option.LEFT || option == Option.BOTH) {
                add(leftTable).height(18f)
            }
            add(textField).growX().height(18f)

            add(rightTable).height(18f).minWidth(6f)

        }

        add(container).grow()


    }

    class IconTextfieldStyle(leftIconName: String?=null,rightIconName:String?=null, rightIconBackground: String = "icon_textfield_right", leftIconBackground:String = "icon_textfield_left", textfieldBg:String = "default", textfieldStyle:String="default" ) {

        var leftIcon: Drawable? = if(leftIconName != null) UI.drawable(leftIconName) else null
        var rightIcon: Drawable? =  if(rightIconName != null) UI.drawable(rightIconName) else null
        var rightIconBackground: Drawable = UI.drawable(rightIconBackground,Color.GRAY)
        var leftIconBackground: Drawable? =  UI.drawable(leftIconBackground,Color.GRAY)
        var textfield_style : String = textfieldStyle
        var icon_bg_tint: Color? = null
        var icon_tint: Color? = null
        var textfield_bg_tint: Color? = null

        val rightButtonStyle : ImageButtonStyle
            get() {
                val style = ImageButtonStyle()
                style.imageUp = rightIcon
                return style
            }

        val leftButtonStyle : ImageButtonStyle
            get() {
                val style = ImageButtonStyle()
                style.imageUp = leftIcon
                return style
            }


    }

}