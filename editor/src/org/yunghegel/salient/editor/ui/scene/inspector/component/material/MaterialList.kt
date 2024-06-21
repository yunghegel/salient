package org.yunghegel.salient.editor.ui.scene.inspector.component.material

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.Selection
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.widget.Separator
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.collections.GdxArray
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.salient.editor.scene.GameObjectSelectionManager
import org.yunghegel.salient.engine.scene3d.component.MaterialsComponent
import org.yunghegel.salient.engine.scene3d.component.ModelComponent
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.*
import kotlin.properties.Delegates

class MaterialList(var withSelection: (Material?)->Unit = {}) : STable() {

    private var materials: GdxArray<Material> = Array()

    val container = STable()
    val rightSection = STable()
    val leftSection = STable()
    val goSelection : GameObjectSelectionManager = inject()
    val bottomRow = STable()

    val materialIconDrawable = UI.drawable("material_file")

    val map = mutableMapOf<Material, MaterialListRow>()

    val selection = Selection<Material>()

    val plus = STextButton("+","large")
    val minus = STextButton("-","large")
    val copy = SImageButton("Copy",null)
    val del = SImageButton("Cleanup",null)

    val selectbox = SSelectBox<Material> { mat -> mat.id}

    var selectedMaterial : Material? by Delegates.observable(null) { _, _, new ->

    }

    val changeActor : Actor = object: Actor() {
        init {
            addListener(object: ChangeListener(){
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    selectedMaterial = selection.first()
                    println("Selected ${selectedMaterial?.id}")
                    withSelection(selectedMaterial)
                }
            })
        }
    }

    init {

        align(Align.top)
        leftSection.align(Align.top)
        rightSection.align(Align.top)
        container.add(leftSection).grow()
        container.add(Separator()).growY()
        container.add(rightSection).growY().width(22f)
        add(container).grow().row()
        add(Separator()).growX().row()
        add(bottomRow).growX().row()
        materials.forEach { addMaterial(it) }
        container.setBackground( UI.drawable("tab_down"))
        bottomRow.setBackground(UI.drawable("tab_panel",Color(0.4f,0.4f,0.4f,1f)))

        with(rightSection) {
            pad(4f,0f,4f,0f)
            add(plus).size(16f).padBottom(4f).row()
            add(minus).size(16f).padBottom(4f).row()
        }

        with(bottomRow) {
            pad(4f,0f,4f,0f)
            add(selectbox).growX().height(18f).padHorizontal(4f)
            add(copy).size(16f).padHorizontal(4f)
            add(del).size(16f).padHorizontal(4f)
        }

        with(selectbox){
            onChange {
                selection.set(selected)

            }
        }

        selection.setActor(changeActor)

        plus.onClick {
            val mat = Material()
            materials.add(mat)
            goSelection.selection.lastSelected?.let{
                it.getComponent(ModelComponent::class.java)?.value?.let { mdl -> mdl.materials.clear(); mdl.materials.add(mat)}
                it.getComponent(MaterialsComponent::class.java)?.materials?.add(mat)
            }
            addMaterial(mat,true)
        }

    }

    fun set(materials: GdxArray<Material> = GdxArray()) {
        leftSection.clearChildren()
        this.materials = materials
        selectbox.setItems(materials)
        materials.forEach {
            val last = materials.indexOf(it) == materials.size - 1
            addMaterial(it,last)
        }
    }

    fun addMaterial(mat: Material,last : Boolean = false) {
        val row = if (mat in map) map[mat]!! else MaterialListRow(mat).also { map[mat] = it }
        leftSection.add(row).growX().row()
        if (!last) leftSection.add(Separator()).growX().row()
    }

    fun handleRowClick(row: MaterialListRow) {
        println("Clicked ${row.mat.id}")
        selection.set(row.mat)
    }

    inner class MaterialListRow(val mat: Material) : STable() {

        val labelTex = mat.id ?: "Material${materials.indexOf(mat)}"

        val label = SLabel(labelTex)
        val materialIcon = SImage(materialIconDrawable)

        var hovered = false
        var selected = false

         val over = UI.drawable("border-light")
         val selectedbg = UI.drawable("button-compact-blue")
         val bg = UI.drawable("button_underline_dark")



        init {

            touchable = Touchable.enabled

            add(materialIcon).size(16f)
            add(label).growX()
            pad(3f)

            addListener(object: ClickListener() {

                override fun enter(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float, pointer: Int, fromActor: com.badlogic.gdx.scenes.scene2d.Actor?) {
                    hovered = true
                    if(!(selection.contains(mat)) ) setBackground(over)
                    super.enter(event, x, y, pointer, fromActor)
                }

                override fun exit(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float, pointer: Int, toActor: com.badlogic.gdx.scenes.scene2d.Actor?) {
                    hovered = false
                    if (!selection.contains(mat)) setBackground(bg)
                    super.exit(event, x, y, pointer, toActor)
                }

                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    handleRowClick(this@MaterialListRow)
                    super.clicked(event, x, y)
                }

            })

        }
    }

}