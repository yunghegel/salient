package org.yunghegel.salient.editor.ui.scene.inspector.component

import com.badlogic.gdx.graphics.g3d.Attribute
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.Separator
import ktx.collections.GdxArray
import org.yunghegel.gdx.utils.ext.colorBox
import org.yunghegel.gdx.utils.ext.each
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.gdx.utils.ext.slider
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.editor.ui.scene.inspector.ComponentInspector
import org.yunghegel.salient.editor.ui.scene.inspector.component.material.AttributeInspector
import org.yunghegel.salient.editor.ui.scene.inspector.component.material.MaterialList
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.component.MaterialsComponent
import org.yunghegel.salient.engine.scene3d.component.ModelComponent
import org.yunghegel.salient.engine.scene3d.component.RenderableComponent
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.label
import org.yunghegel.salient.engine.ui.layout.CollapsePanel
import org.yunghegel.salient.engine.ui.layout.Panel
import org.yunghegel.salient.engine.ui.scene2d.SImage
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.table
import org.yunghegel.salient.engine.ui.widgets.FloatField
import org.yunghegel.salient.engine.ui.widgets.list.InteractiveList
import org.yunghegel.salient.engine.ui.widgets.list.TestList
import org.yunghegel.salient.engine.ui.widgets.value.FieldEditor.Companion.formatName
import org.yunghegel.salient.engine.ui.widgets.value.ReflectionBasedEditor

class MaterialInspector : ComponentInspector<MaterialsComponent, GdxArray<Material>>(MaterialsComponent::class.java, "Materials", "material_object") {
    val scene : Scene = inject()

    val inspectors : Panel = Panel()
    val container = STable()

    val list = object : InteractiveList<Material,GameObject>() {

        override val controller: Controller<Material, GameObject> = object : Controller<Material, GameObject> {
            override fun create(): Material {
                return Material()
            }

            override fun plus(item: Material) {

            }

            override fun minus(item: Material) {

            }

            override fun copy(item: Material): Material {
               return item.copy()
            }

            override fun selected(item: Material?, applyTo: GameObject?) {
                populateAttributesFrom(item)
            }
        }

        override fun createRow(item: Material): Row {
            return object : Row(item) {
                val icon = UI.drawable("materials_icon")
                override fun buildActor() {
                    add(SImage(icon,16)).padHorizontal(4f)
                    add(label(item.id)).growX().row()
                }
            }
        }

        override fun itemToString(item: Material): String {
            return item.id
        }

        override fun getPrefWidth(): Float {
            val parentW  = parent?.width ?: 0f
            return parentW * 0.7f
        }
    }

    val matList = MaterialList { mat ->
        populateAttributesFrom(mat)
    }

    init {


        align(Align.top)
    }



    override fun populate(component: MaterialsComponent?) {
        if (component != null ) {
            list.set(component.materials)
//            component.materials.each { mat ->
//                val row = list.createRow(mat)
//                list.listItems.add(row)
//            }
        }
    }

    fun populateAttributesFrom(mat:Material?) {
        if (mat != null) {
            container.clearChildren()
            val discoveredTypes : MutableSet<Class<out Attribute>> = HashSet()
            val map = mutableMapOf<Class<out Attribute>,Panel>()
            selectedGameObject?.get(RenderableComponent::class)?.let { model ->
                val instance = model.value as ModelInstance?
                instance?.materials?.forEach { mat ->
                    mat.forEach { attr ->

                        if (!discoveredTypes.contains(attr::class.java)) {
                            discoveredTypes.add(attr::class.java)
                            map[attr::class.java] = Panel().apply {
                                createTitle(formatName(attr::class.simpleName!!),attr::class.simpleName!!)
                                container.add(this).grow().pad(4f).row()
                            }
                        }

                        if (attr is FloatAttribute) {
                            map[attr::class.java]!!.add(table {
                                add(label(formatName(Attribute.getAttributeAlias(attr.type)))).right()
                                add(FloatField(
                                    name = "",
                                    negative = false,
                                    setter = { attr.value = it },
                                    accessor = { attr.value }
                                )).growX().right()

                            }).grow().padHorizontal(4f).align(Align.left).row()
                        }
                        if (attr is ColorAttribute) {
                            map[attr::class.java]!!.add(table {
                                skin = this@MaterialInspector.skin
                                add(label(formatName(Attribute.getAttributeAlias(attr.type)))).left().growX()
                                colorBox(this, attr.color, true, skin)
                            }).grow().row()
                        }
                    }
                }
            }

        }
    }

    override fun createLayout() {
        align(Align.top)
        val panel = CollapsePanel("Attributes", contentActor = container)

        add(list).pad(10f).grow().top().row()
        add(panel).grow().pad(10f).row()

    }
}

