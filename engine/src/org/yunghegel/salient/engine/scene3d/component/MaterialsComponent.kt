package org.yunghegel.salient.engine.scene3d.component

import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import ktx.collections.GdxArray
import net.mgsx.gltf.scene3d.utils.MaterialConverter
import org.yunghegel.gdx.utils.ext.convertToPBR
import org.yunghegel.gdx.utils.ext.each
import org.yunghegel.salient.engine.api.ecs.BaseComponent
import org.yunghegel.salient.engine.api.ecs.ComponentCloneable
import org.yunghegel.salient.engine.ui.Icon
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.scene3d.GameObject
import kotlin.reflect.KClass

class MaterialsComponent(val materials: GdxArray<Material>,go: GameObject) : EntityComponent<GdxArray<Material>>(materials,go), ComponentCloneable<MaterialsComponent>
    {

        init {

        }

        override var value: GdxArray<Material>?
            get() = go[RenderableComponent::class]?.let { comp ->
                if (comp.value is ModelInstance?) {
                   (comp.value as ModelInstance?)?.materials
                } else null
            }
            set(value) {
                go[RenderableComponent::class]?.let { comp ->
                    val renderable = comp.value
                    if (renderable is ModelInstance?) {
                        renderable?.materials!!.clear()
                        renderable?.materials!!.addAll(value)
                    }
                }
            }

        override fun onComponentAdded(go: GameObject) {
            super.onComponentAdded(go)
        }

        override val iconName: String = "material_object"

    override val type: KClass<out BaseComponent> = MaterialsComponent::class

    override fun clone(target: GameObject): MaterialsComponent {
        val mats = GdxArray.with<Material>()
        materials.each { mat ->
            val newMat = mat.copy()
            mats.add(newMat)
        }
        return MaterialsComponent(mats,target)
    }

        override fun apply(comp: MaterialsComponent, target: GameObject) {
            val model = target[ModelComponent::class] ?: throw Exception("Model must be present first")
            model.value?.let { mdl ->
                comp.materials.each { mat ->
                    mdl.materials.add(mat)
                }
            }
            target.add(comp)
        }

        constructor(material: Material,go: GameObject) : this(GdxArray.with(material),go)

}