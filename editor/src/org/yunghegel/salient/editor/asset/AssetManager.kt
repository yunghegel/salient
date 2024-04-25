package org.yunghegel.salient.editor.asset

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import org.yunghegel.salient.engine.api.EditorAssetManager


class AssetManager() : EditorAssetManager {

    private val loader : Loader =  Loader()

    val progress : Float
        get() = MathUtils.clamp(loader.getProgress() + 0.02F, 0f, 1f);



}