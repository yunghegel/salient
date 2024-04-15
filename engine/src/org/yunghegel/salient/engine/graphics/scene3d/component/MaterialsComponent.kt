package org.yunghegel.salient.engine.graphics.scene3d.component

import com.badlogic.gdx.graphics.g3d.Material
import ktx.collections.GdxArray
import org.yunghegel.salient.engine.ui.Icon
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.graphics.scene3d.GameObject

class MaterialsComponent(val materials: GdxArray<Material>,go: GameObject) : EntityComponent<GdxArray<Material>>(null, materials,go) ,
    Icon {

    override val iconDrawableName: String = "material"

    constructor(material: Material,go: GameObject) : this(GdxArray.with(material),go)

}