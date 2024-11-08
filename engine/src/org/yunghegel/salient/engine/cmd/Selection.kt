package org.yunghegel.salient.engine.cmd

import ktx.collections.GdxArray
import org.checkerframework.checker.units.qual.s
import org.yunghegel.gdx.cli.CommandSet
import org.yunghegel.gdx.cli.arg.Arg
import org.yunghegel.gdx.cli.arg.Cmd
import org.yunghegel.gdx.cli.arg.Namespace
import org.yunghegel.gdx.cli.arg.Opt
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.component.MaterialsComponent
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.system.warn
import org.yunghegel.salient.engine.ui.widgets.value.ReflectionBasedEditor.AccessorRegistry.name

@Namespace("selection")
class Selection : CommandSet<GameObject>{

    override fun injectDependency(): GameObject? {
        val go : GameObject = inject()
        if ( go == go.scene.graph.root) {
            warn("Root object cannot be selected")
            return null
        }
        return go
    }

    val get : GameObject? get() {
        val go = inject<GameObject>()

        return if (go != go.scene.graph.root) go else null
    }

    @Cmd("describe")
    fun describe() {
        println(get)
    }

    @Cmd("choosename")
    fun choosename(@Arg("name") name: String) {
        if (get == null) return
        get!!.scene.graph.find(name)?.let { go ->
            go.scene.selection.select(go)
        }
    }

    @Cmd("chooseid")
    fun chooseid(@Arg("id") id: Int) {
        if (get == null) return
        get!!.scene.graph.find(id)?.let {
            it.scene.selection.select(it)
        }
    }

    @Cmd("material_clear")
    fun clear_material() {
        if (get==null) return
        get!![MaterialsComponent::class]?.let { it.value?.forEach {
            it.forEach { print("$it ") }
            it.clear()
        }}
    }

    fun set_attr(@Arg("alias") alias: String, @Arg("value") value: Any) {

    }




}

fun GameObject.ifNotRoot(fn: ()->Unit) {
    if (scene.graph.root != this) fn()
}