package org.yunghegel.salient.engine.ui.widgets
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ObjectSet
import ktx.actors.onChange
import mobx.core.autorun

import org.yunghegel.gdx.utils.data.EnumBitmask
import org.yunghegel.gdx.utils.data.PredicatedBitmaskAction
import org.yunghegel.salient.engine.ui.scene2d.SCheckBox
import org.yunghegel.salient.engine.ui.scene2d.STable
import java.util.*

typealias EnumSetAction<T> = (EnumBitmask<T>) -> Unit

class BitmaskConfigWidget<T : Enum<T>>(val enumMask: EnumBitmask<T>,val newRowAfter: Int? = null, var onChange: EnumSetAction<T>? = null) :
    STable(), PredicatedBitmaskAction<T> {

    private val objSet: ConfigActorSet<T, ConfigActor<T>> = ConfigActorSet()

//    val trueEnums: EnumSet<T> by computed {
//        val set = enumMask.copyFromMask(enumMask.mask)
//        val enumSet = EnumSet.noneOf(enumMask.enumClass)
//        set.forEachTrue {
//            enumSet.add(it)
//        }
//        enumSet
//    }

    val trueSet: EnumSet<T> = EnumSet.noneOf(enumMask.enumClass)

    val falseSet: EnumSet<T>
        get() {
            return EnumSet.complementOf(trueSet)
        }

    init {
        autorun {
            val mask = enumMask.mask
            onChange?.let { it(enumMask) }

        }

       EnumSet.allOf(enumMask.enumClass).forEach { enum ->
            val actor = ConfigActor(enum, enumMask)

            objSet.add(actor)
        }

        val group = VerticalGroup()
        group.grow().space(5f).columnLeft()
        var index = 1
        objSet.forEach { actor ->
           val cell=  add(actor).left().growX().padBottom(5f)
            if (newRowAfter != null && index++ % newRowAfter ==0) {
                cell.row()
            }

        }
        align(Align.left)

        add(group).grow().pad(4f)

    }

    override fun action(enum: T, value: Boolean) {

        objSet[enum]?.isChecked = value

    }

    fun ensureActor(enum: T) {

    }

    inner class ConfigActorSet<T : Enum<T>, K : ConfigActor<T>> : ObjectSet<K>() {

        operator fun get(enum: T): K? {
            forEach {
                if (it.enum == enum) return it
            }
            return null
        }

    }

}

class ConfigActor<T : Enum<T>>(val enum: T, enumMask: EnumBitmask<T>) : SCheckBox(enum.name) {

    init {
        setProgrammaticChangeEvents(false)

        isChecked = enumMask.get(enum)
        labelCell.padLeft(5f).growX()
        onChange {
            enumMask.toggle(enum)
        }

//        proper noun case
        setText(enum.name.lowercase().split("_").joinToString(" ") { it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } })
    }

}