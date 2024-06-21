package org.yunghegel.salient.editor.ui.scene.inspector.component.material

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Attribute
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor
import com.badlogic.gdx.scenes.scene2d.Actor
import com.kotcrab.vis.ui.widget.VisTextField
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.gdx.utils.ext.textureDrawable
import org.yunghegel.gdx.utils.reflection.FieldAccessor
import org.yunghegel.gdx.utils.ui.ColorBox
import org.yunghegel.gdx.utils.ui.ColorPicker
import org.yunghegel.salient.editor.asset.AssetManager
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.engine.api.asset.type.AssetType
import org.yunghegel.salient.engine.api.asset.type.TextureAsset
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.*
import org.yunghegel.salient.engine.ui.widgets.value.FieldEditor
import org.yunghegel.salient.engine.ui.widgets.value.widgets.ColorEditor
import org.yunghegel.salient.engine.ui.widgets.value.widgets.FloatEditor
import org.yunghegel.salient.engine.ui.widgets.value.widgets.FloatOnlyFilter


abstract class AttributeInspector<T:Attribute, V:Any>(val attr: T) : STable(){

    val title : String = FieldEditor.formatName(Attribute.getAttributeAlias(attr.type))
    val label = SLabel(title)



    abstract fun createUI(mat: Material)

    abstract fun getValue() : V

    abstract fun setValue(value: V)

    companion object {
        fun createInspector(attr: Attribute) : AttributeInspector<*,*>? {
            return when(attr) {
                is PBRColorAttribute -> ColorAttrInspector(attr)
                is PBRFloatAttribute -> FloatAttrInspector(attr)
                else -> null
            }
        }

        fun buildUI(mat:Material,table:STable)  {
            mat.forEach { attr ->
                println(Attribute.getAttributeAlias(attr.type))
                val inspector = createInspector(attr)
                if (inspector != null) {
                    inspector.createUI(mat)
                    table.add(inspector).growX().row()

                }
            }
        }
    }

}

class ColorAttrInspector(attr: PBRColorAttribute) : AttributeInspector<PBRColorAttribute, Color>(attr) {



    private val colorBox = ColorBox("color",{attr.color},true,skin,16f)

    val icon = SImageButton("color")

    override fun createUI(mat: Material) {
        add(icon).size(16f).padHorizontal(4f)
        add(label).growX()
        add(colorBox).growX()
        colorBox.setColor(attr.color)
        colorBox.callback = {col ->
            println("${attr.color} | $col")
            setValue(col)
        }
    }

    override fun getValue(): Color {
        return attr.color
    }

    override fun setValue(value: Color) {
        attr.color.set(value )
    }


}

class FloatAttrInspector(attr: PBRFloatAttribute) : AttributeInspector<PBRFloatAttribute, Float>(attr) {
        val validator = FloatOnlyFilter()
        val editor = STextField(attr.value.toString()) { text ->
            try {
                val value = text.toFloat()
                attr.value = value
            } catch (e: NumberFormatException) {
                org.yunghegel.salient.engine.system.error("Invalid float value: $text")
            }
        }

    init {
        editor.setTextFieldFilter(validator)

    }

    override fun createUI(mat: Material) {
        add(label).growX()
        add(editor).growX().maxWidth(50f)
    }

    override fun getValue(): Float {
        return attr.value
    }

    override fun setValue(value: Float) {
        attr.value = value
    }

}

class TextureAttrInspector(attr: TextureAttribute) : AttributeInspector<TextureAttribute, TextureDescriptor<Texture>>(attr) {

    val assetManager : AssetManager = inject()

    val icon = SImageButton("texture")
    val image = SImage(textureDrawable(attr.textureDescription.texture))

    override fun createUI(mat: Material) {
        add(icon).size(16f).padHorizontal(4f)
        add(label).growX()
        add(image).growX()
    }

    override fun getValue(): TextureDescriptor<Texture> {
        return attr.textureDescription
    }

    override fun setValue(value: TextureDescriptor<Texture>) {
        attr.textureDescription.set(value)
    }

    fun loadNewTexture(path: String) {
        val handle = AssetHandle(path)
        KtxAsync.launch {
            val texture  = assetManager.storage.load<Texture>(path)

            attr.textureDescription.texture = texture
            image.drawable = textureDrawable(texture)
            assetManager.indexHandle(handle, inject())
        }
    }

    fun getExistingTextures() : List<TextureAsset> {
        val scene : Scene = inject()
        return scene.assets.filterIsInstance<TextureAsset>()
    }

    data class Ref (val handle: TextureAsset, val texture: Texture)

}