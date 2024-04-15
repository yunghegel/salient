package org.yunghegel.salient.engine.ui.widgets.value

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.scene2d.*
import org.yunghegel.gdx.utils.data.Searchable
import org.yunghegel.gdx.utils.ext.tip
import org.yunghegel.gdx.utils.reflection.Editable
import org.yunghegel.gdx.utils.reflection.FieldAccessor
import org.yunghegel.gdx.utils.ui.LabelSupplier
import org.yunghegel.salient.engine.ui.widgets.value.FieldEditor.Companion.formatName

abstract class DefaultFieldEditor : Table(), FieldEditor, Searchable, LabelSupplier {


    var labelActor : Label? = null

    private var actor : Actor? = null

    var accessor: FieldAccessor? = null

    var fieldName : String = ""

    var group = "null"

    override var label: String? = fieldName



    override val searchTerms: List<String>
        get() = listOf(labelActor?.text.toString(), accessor?.getName() ?: "", accessor?.getType()?.typeName ?: "", if (group!=null) group else "").
    filter { it.isNotEmpty() }

    init {

    }

    override fun create(accessor: FieldAccessor,parser: (Editable)->Unit): DefaultFieldEditor {
        this.accessor = accessor
        val table = this


        val config = parseConfig(accessor) { config ->
            parser(config)
            if (config.readonly) {
                table.touchable = Touchable.disabled
                val lock = scene2d.imageButton("lock")
                lock.isChecked = true
                lock.touchable = Touchable.disabled
                table.add(lock).padRight(5f)
            }
            if(config.tooltip.isNotEmpty()) {
                table.tip { add(scene2d.table { label(config.tooltip) }) }
            }
        }
        labelActor = createLabel(accessor,config)
        actor = createEditable(accessor)


        labelActor?.let { label ->
            labelDefault(table.add(label))
        }
        actorDefault(table.add(actor))

        return table
    }
    open fun createLabel(accessor: FieldAccessor,editable: Editable? = null) : Label? {
        fieldName = editable?.name ?: formatName(accessor.getName())
        val label = Label(fieldName, Scene2DSkin.defaultSkin)
        editable?.let {  config ->
            if(config.tooltip.isNotEmpty()) {
                label.tip { add(scene2d.table { label(config.tooltip) }) }
            }
        }
        return label
    }

    abstract fun createEditable(accessor: FieldAccessor) : Actor

}