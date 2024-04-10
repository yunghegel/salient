package org.yunghegel.salient.engine.scene3d.component

import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.utils.Array
import ktx.collections.GdxArray
import org.yunghegel.salient.engine.ecs.EntityComponent

class MaterialsComponent(val materials: GdxArray<Material>) : EntityComponent<GdxArray<Material>>(null, materials) {

    constructor(material: Material) : this(GdxArray.with(material))

}