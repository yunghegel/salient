package org.yunghegel.salient.editor.plugins.picking

import com.badlogic.ashley.core.Component
import org.yunghegel.gdx.utils.selection.Pickable

class PickablesBag(val pickables : List<Pickable>) : Component {
    var picked : (Pickable) -> Unit = { println("picked ${it.id}") }
}