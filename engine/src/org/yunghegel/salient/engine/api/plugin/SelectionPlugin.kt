package org.yunghegel.salient.engine.api.plugin

import com.badlogic.ashley.core.Entity
import ktx.collections.GdxArray

abstract class SelectionPlugin {

    abstract fun getSelection(entities: GdxArray<Entity>, screenX: Float, screenY: Float): Int

}