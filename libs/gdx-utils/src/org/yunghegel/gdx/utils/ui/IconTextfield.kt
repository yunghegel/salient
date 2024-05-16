package org.yunghegel.gdx.utils.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.scene2d.Scene2DSkin
import org.yunghegel.gdx.utils.ext.drawable


class IconTextfield(option: Option = Option.LEFT, skin: Skin = Scene2DSkin.defaultSkin, icon: String, text:String )  :
    Table(skin) {

    enum class Option {
        LEFT,RIGHT,BOTH
    }

    val style = IconTextfieldStyle(icon, textfieldStyle = "default")

    val leftIcon = if(option == Option.LEFT || option == Option.BOTH) ImageButton(style.leftButtonStyle) else null
    val rightIcon = if(option == Option.RIGHT || option == Option.BOTH) ImageButton(style.rightButtonStyle) else null

    val leftTable = Table(skin)
    val rightTable = Table(skin)
    val textField = TextField(text,skin,style.textfield_style)

    val input : String
        get() = textField.text

    var listener: IconTextfieldListener? = null
        private set

    interface IconTextfieldListener {
        fun onLeftIconClick()
        fun onRightIconClick()
        fun onTextChange(text: String)

        class Builder {
            private var onLeftIconClick: () -> Unit = {}
            private var onRightIconClick: () -> Unit = {}
            private var onTextChange: (String) -> Unit = {}

            fun onLeftIconClick(onLeftIconClick: () -> Unit) {
                this.onLeftIconClick = onLeftIconClick
            }

            fun onRightIconClick(onRightIconClick: () -> Unit) {
                this.onRightIconClick = onRightIconClick
            }

            fun onTextChange(onTextChange: (String) -> Unit) {
                this.onTextChange = onTextChange
            }

            fun build(build: Builder.()->Unit) : IconTextfieldListener {
                val builder = Builder()
                builder.build()
                return object : IconTextfieldListener {
                    override fun onLeftIconClick() {
                        builder.onLeftIconClick()
                    }

                    override fun onRightIconClick() {
                        builder.onRightIconClick()
                    }

                    override fun onTextChange(text: String) {
                        builder.onTextChange(text)
                    }
                }
            }
        }

        companion object {}
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

    fun listen(lister: IconTextfieldListener.Builder.()->Unit) {
        setListener(IconTextfieldListener.Builder().build(lister))
    }

    init {
        leftTable.background = style.leftIconBackground
        rightTable.background = style.rightIconBackground
        leftIcon?.let { leftTable.add(it) }
        rightIcon?.let { rightTable.add(it)}

        val container = Table(skin)
        with(container) {
            if (option == Option.LEFT || option == Option.BOTH) {
                add(leftTable).height(18f)
            }
            add(textField).growX().height(18f)

            add(rightTable).height(18f).minWidth(6f)

        }

        add(container).grow()


    }

    inner class IconTextfieldStyle(leftIconName: String?=null,rightIconName:String?=null, rightIconBackground: String = "icon_textfield_right", leftIconBackground:String = "icon_textfield_left", textfieldBg:String = "default", textfieldStyle:String="default" ) {

        var leftIcon: Drawable? = if(leftIconName != null) skin.getDrawable(leftIconName) else null
        var rightIcon: Drawable? =  if(rightIconName != null) skin.getDrawable(rightIconName) else null
        var rightIconBackground: Drawable = skin.drawable(rightIconBackground,Color.GRAY)
        var leftIconBackground: Drawable? =  skin.drawable(leftIconBackground,Color.GRAY)
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