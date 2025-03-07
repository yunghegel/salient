package org.yunghegel.salient.engine.ui.tree

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions.delay
import com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Tree
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import dev.lyze.gdxtinyvg.utils.WhitePixelUtils
import ktx.actors.onClick
import org.yunghegel.gdx.utils.data.Named
import org.yunghegel.gdx.utils.ext.notnull
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.salient.engine.ui.scene2d.SImage
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.widgets.EditableTextField
import org.yunghegel.salient.engine.ui.widgets.OptionMenu

abstract class TreeActor<Object>(val obj: Object) : STable()  {

    val action = MoveToAction()
    var dragging = false
    val origin = Vector2()

    var visualIndex = -1
    val overflow = SImageButton("overflow-menu")
    private val options = mutableListOf<OptionMenu.Option>()

    open val pixel: TextureRegion = WhitePixelUtils.createWhitePixelTexture()

    var nodename: String?=null
        set(value) {
            field = value
            title.setText(value ?: "")
        }


    var node: TreeNode<*,*> by notnull()

    open var title : EditableTextField = EditableTextField(if (obj is Named) obj.name else obj.toString())
    open var icon : Drawable? = null
    open var iconImage : SImage by notnull()
    val actors = mutableListOf<Actor>()

    init {
        touchable = Touchable.enabled
    }

    fun prependActor(actor: Actor) : Cell<out Actor> {
        val children = children
        val cell : Cell<out Actor>
        if (children.size > 0) {
            val first = children[0]
            addActorBefore(first, actor)
            cell = getCell(actor)
        } else {
            cell = add(actor)
        }
        return cell
    }

    abstract fun buildActor(obj: Object)

    fun addChildActor(actor: Actor) {
        actors.add(actor)
    }

    fun createOverflowMenu(configIconCell: (Cell<*>) -> Unit = {}){
        add(overflow).padHorizontal(2f)
        overflow.onClick {
            val menu = OptionMenu(configIconCell)
            options.forEach { menu.addOption(it) }
            menu.showMenu(stage, overflow)
        }
    }

    fun addOverflowOption(name : String, icon : String?, action: () -> Unit) {
        options.add(OptionMenu.option(name, icon, action))
    }

    fun drag(x:Int,y:Int) {
        val hit = hit(x.toFloat(), y.toFloat(), true)
        if (hit != null) {
            dragging = true
            val actor = hit
            action.setPosition(x.toFloat(), y.toFloat())
            addAction(action)
        }
    }

    fun setOrigin(node: Tree.Node<*,*,*>) {
        val actor = node.actor
        origin.set(actor.x, actor.y)
    }

    fun drop(x:Int,y:Int) {
        if (dragging) {
            dragging = false
            val hit = hit(x.toFloat(), y.toFloat(), true)
            val moveTo = MoveToAction().apply {
                setStartPosition(x.toFloat(), y.toFloat())
                interpolation =                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       Interpolation.bounceOut
            }
            moveTo.setPosition(origin.x, origin.y)
            addAction(sequence(delay(100f), moveTo))
        }
    }

    fun ensureReturn() {
        if (!Gdx.input.isButtonPressed(0)) {
            dragging = false
            val moveTo = MoveToAction().apply {
                setStartPosition(x.toFloat(), y.toFloat())
                interpolation = Interpolation.bounceOut
            }
            moveTo.setPosition(origin.x, origin.y)
            addAction(sequence(delay(100f), moveTo))
            stage?.cancelTouchFocus()
        }
    }

    override fun act(delta: Float) {
        if (dragging) ensureReturn()
        super.act(delta)
    }

}