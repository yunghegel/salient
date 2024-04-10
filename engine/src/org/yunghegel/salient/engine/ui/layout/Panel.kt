package org.yunghegel.salient.ui.container

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.Separator
import org.yunghegel.salient.engine.ui.Icons
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.scene2d.SImageButton


open class Panel() : STable() {

    internal var titleTable: TitleTable
    internal var bodyTable: STable

    var hidden = false

    private var iconify : (Boolean) -> Unit = {}

    override fun <T:Actor> add(actor: T): Cell<T> {
        return bodyTable.add(actor)
    }

    fun <T:Actor> addInternal(actor: T): Cell<T>? {
        return super.add(actor)
    }

    fun iconify() {
        iconify(hidden)
    }

    fun setIconifyAction(action: (Boolean) -> Unit) {
        iconify = action
    }

    fun createIcon(name: String) {
        titleTable.createIcon(name)
    }

    fun createIcon(drawable:TextureRegionDrawable) {
        titleTable.createIcon(drawable)
    }

    fun createTitle(title: String,category:String) {
        titleTable.createTitle(title,category)
    }

    fun addButton(button: Button) {
        titleTable.addButton(button)
    }

    init {
        align(Align.left)
        titleTable = TitleTable()
        titleTable.align(Align.left)
        addInternal(titleTable)!!.growX().left().height(24f)
        row()
        addInternal(Separator())!!.growX().row()
        bodyTable = STable()
        bodyTable.align(Align.topLeft)
        addInternal(bodyTable)!!.grow().row()
        bodyTable.touchable = Touchable.enabled
    }

    internal class TitleTable : STable() {

        val container = STable()

        var category : String = ""

        var iconName : String? = null
        var titleName : String? = null

        var icon : ImageButton? = null
        var title : SLabel? = null

        var buttons = mutableListOf<Button>()



        val iconifyWindow = ImageButton(Icons.WINDOW_ICONIFY.drawable)

        init {
            add(container).growX().left().pad(2f)
            container.align(Align.left)
            setBackground("dark-gray")
            iconifyWindow.style.down = Icons.WINDOW_ICONIFY_OVER.drawable
            iconifyWindow.style.over = UI.skin.getDrawable("border-y")

        }

        fun createIcon(name: String) {
            iconName = name
            this.icon = SImageButton(name)
            val copy = ImageButtonStyle(icon?.style)
            icon?.style = copy
            icon?.style?.up = null
            icon?.style?.down = null
            icon?.style?.over = null
            rebuild()
        }

        fun createIcon(drawable:TextureRegionDrawable) {
            this.icon = ImageButton(drawable)

            rebuild()
        }

        fun createTitle(title: String,category:String) {
            this.category = category
            titleName = title
            this.title = SLabel(title)
            rebuild()
        }

        fun addButton(button: Button) {
            buttons.add(button)
            rebuild()
        }

        fun rebuild() {
            container.clear()
            children.filterIsInstance<Button>().forEach { removeActor(it) }
            if(icon != null) container.add(icon).size(18f).pad(2f)
            if(title != null) container.add(title).pad(2f)
            buttons.forEach { add(it).pad(2f) }

        }

    }

}