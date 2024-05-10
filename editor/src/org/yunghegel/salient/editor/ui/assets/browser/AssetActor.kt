package org.yunghegel.salient.editor.ui.assets.browser

import assimp.format.assxml.textureToMap
import com.badlogic.gdx.graphics.Texture
import org.yunghegel.gdx.utils.ext.textureDrawable
import org.yunghegel.gdx.utils.ui.LabelSupplier
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.api.asset.type.ModelAsset
import org.yunghegel.salient.engine.api.asset.type.TextureAsset
import org.yunghegel.salient.engine.helpers.PreviewImage
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.scene2d.SImage
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable

class AssetActor(val asset : Asset<*>) : STable(), LabelSupplier {

    override var label : String? = asset.handle.name

    var thumbnail: SImage? = null

    init {
        val tex : Texture? = when (asset) {
            is ModelAsset -> asset.value?.let { model -> PreviewImage(model,inject()).generate() }
            is TextureAsset -> asset.value
            else -> null
        }

        tex?.let { texture ->
            thumbnail = SImage(textureDrawable(texture),100 )
        }

        val name = SLabel(label!!)

    }

}