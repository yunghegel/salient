package org.yunghegel.salient.editor.ui.assets.browser

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload
import com.badlogic.gdx.scenes.scene2d.utils.Selection
import com.badlogic.gdx.utils.Scaling
import org.yunghegel.gdx.utils.ext.*
import org.yunghegel.gdx.utils.ui.LabelSupplier
import org.yunghegel.salient.editor.app.Gui
import org.yunghegel.salient.editor.plugins.gizmos.tools.PlacementTool
import org.yunghegel.salient.editor.plugins.intersect.tools.IntersectorTool
import org.yunghegel.salient.editor.plugins.picking.tools.HoverTool
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.api.asset.type.AssetType
import org.yunghegel.salient.engine.api.asset.type.ModelAsset
import org.yunghegel.salient.engine.api.asset.type.TextureAsset
import org.yunghegel.salient.engine.helpers.PreviewImage
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.SImage
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.table

class AssetActor(val asset : Asset<*>) : STable(), LabelSupplier {

    override var label : String? = asset.handle.name

    var thumbnail: SImage? = null

    private val tex : Texture?

    val inactive = UI.drawable("thumbnail", Color.WHITE.cpy().alpha(0.1f))
    val active = UI.drawable("thumbnail")



    var sizing = 1f
        set(value) {
            field = value

        }

    val hovertool : HoverTool by lazy  { inject() }
    val intersectionTool : IntersectorTool by lazy { inject() }
    val placementTool : PlacementTool by lazy { inject() }
    val gui : Gui by lazy {inject()}

    var selected : Boolean = false
        set(value) {
            field = value
            background = if (value) active else inactive
        }
    var initialized = false

    init {
        tex = when (asset) {
            is ModelAsset -> asset.value?.let { model -> PreviewImage(model,inject()).generate() }
            is TextureAsset -> asset.value
            else -> null
        }

        touchable = Touchable.enabled

        val headertext = "${asset.handle.name}"
        val name = object:SLabel(headertext,"default-small") {
            override fun getPrefWidth(): Float {
                return this@AssetActor.width-20f-8f
            }
        }

        name.setEllipsis(true)

            add(SImage(AssetType.iconFor(asset.assetType), 16)).size(16f).padLeft(5f)
            add(name).pad(4f).growX().row()


        tex?.let { texture ->
            thumbnail = SImage(textureDrawable(texture),100 )
            thumbnail!!.setScaling(Scaling.fit)
            add(thumbnail).grow().padHorizontal(3f).colspan(2)
        }

        background = inactive
    }


    override fun getPrefWidth(): Float {
        return super.getPrefWidth()*sizing
    }

    override fun getPrefHeight(): Float {
        return super.getPrefHeight()*sizing
    }

    fun select(selection: Selection<AssetActor>) {
            selection.set(this)
            selected = true
    }

    fun deselect(selection: Selection<AssetActor>) {
        selected = false
        selection.remove(this)
    }

    fun createDND(dnd: DragAndDrop) : DragAndDrop.Source {
        val source = object: DragAndDrop.Source(this) {
        var pl : Payload? = null
            override fun dragStop(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                payload: DragAndDrop.Payload?,
                target: DragAndDrop.Target?
            ) {
                super.dragStop(event, x, y, pointer, payload, target)
                pl = payload
                background = inactive
                placementTool.stop()
                hovertool.deactivate()
            }

            override fun dragStart(p0: InputEvent?, p1: Float, p2: Float, p3: Int): DragAndDrop.Payload {
                val payload  = Payload()
                pl = payload
                payload.`object` = asset
                background = active
                if (tex != null && asset.assetType == AssetType.Texture) {
                    payload.dragActor = SImage(textureDrawable(tex),100 )
                    payload.dragActor.moveBy(-50f,-50f)
                } else {
                    payload.dragActor = SLabel(asset.handle.name,"bold")
                }
                if (asset is ModelAsset) {
                    placementTool.start(asset)
                } else {
                    hovertool.activate()
                }

                return payload
            }

            override fun drag(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                hovertool.query(false) { over ->
                    println(over)
                }
                if (asset.assetType == AssetType.Model) {
                    val hit = (stage.hit(x,y,false))
                    hit?.let {
                        if (hit.isDescendantOf(gui.viewportWidget)) {
                            pl?.dragActor = SLabel(asset.handle.name,"bold")
                        }
                    }

                    val (screenX,screenY) = Pair(x,y).int()
                }
                super.drag(event, x, y, pointer)
            }
        }
        dnd.addSource(source)
        return source

    }

}