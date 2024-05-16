package org.yunghegel.salient.engine.scene3d.component

import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import ktx.collections.GdxArray
import net.mgsx.gltf.scene3d.utils.MaterialConverter
import org.yunghegel.gdx.utils.ext.convertToPBR
import org.yunghegel.gdx.utils.ext.each
import org.yunghegel.salient.engine.ui.Icon
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.scene3d.GameObject

class MaterialsComponent(val materials: GdxArray<Material>,go: GameObject) : EntityComponent<GdxArray<Material>>(materials,go) ,
    Icon {

        init {
            materials.each { mat ->
                mat.forEach { println(it::class) }
                convertToPBR(mat)

            }
        }

    override val iconDrawableName: String = "material"

    constructor(material: Material,go: GameObject) : this(GdxArray.with(material),go)

}