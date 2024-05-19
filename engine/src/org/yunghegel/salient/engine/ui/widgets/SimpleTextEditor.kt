package org.yunghegel.salient.engine.ui.widgets

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.Separator
import ktx.actors.onChange

class SimpleTextEditor(skin : Skin, title:String) : Table(skin) {

    val titlecontainer = Table(skin)

    val titleActors = Table(skin)

    val gutter = Table(skin)

    val editorcontainer = Table(skin)

    val textArea = object : TextArea(null,skin,"textArea") {
        override fun setText(str: String?) {
            val sanitized = str?.replace("\r", "") ?: text
            super.setText(sanitized)
        }
    }

    val selectBox = SelectBox<String> (skin,"dark")
    val current: TextField = TextField("depth.frag",skin,"toast")

    init {
        gutter.background = skin.getDrawable("text_editor_gutter")
        gutter.pad(7f,10f,0f,10f)
        gutter.align(Align.top)

        editorcontainer.background = skin.getDrawable("text_editor_background")
        editorcontainer.pad(10f,10f,0f,10f)

        titlecontainer.align(Align.left)
        titlecontainer.add(Label("GLSL", skin)).growX().height(20f).padRight(40f).left()
        titlecontainer.add(titleActors).growX().height(20f)

        val pane = ScrollPane(textArea, skin)
        editorcontainer.add(pane).grow()

        add(titlecontainer).height(24f).colspan(3).row()
        add(Separator()).growX().colspan(3).row()
        add(gutter).growY().width(40f)
        add(Separator()).growY().width(1f)
        add(editorcontainer).grow()

        creatTitleActors()
        val textHeight = textArea.style.font.getCapHeight() - textArea.style.font.getDescent() * 2
        val lineNumbers = Label("1", skin)
        lineNumbers.touchable = Touchable.disabled
        lineNumbers.setAlignment(Align.topRight)
        lineNumbers.setSize(40f, textHeight)
        gutter.add(lineNumbers).growY()
        textArea.setTextFieldListener { _, character ->
            println("${character.category.code}")
            val lines = textArea.text.split("\n")
            val lineCount = lines.size
            lineNumbers.setText((1..lineCount).joinToString("\n"))
        }
    }

    fun creatTitleActors() {
        selectBox.setItems("Vertex","Fragment")
        titleActors.add(selectBox).pad(2f,4f,2f,4f)
        titleActors.add(current).pad(2f,4f,2f,4f).width(100f).pad(0f,15f,0f,15f)
        menuOption("Save")
        menuOption("Compile")
        menuOption("Run")
    }

    fun menuOption(text:String,action:()->Unit={}) {
        val btn = TextButton(text,skin,"soft-blue")
        btn.onChange {
            action()
            true
        }
        titleActors.add(btn).pad(2f,4f,2f,4f)
    }

}