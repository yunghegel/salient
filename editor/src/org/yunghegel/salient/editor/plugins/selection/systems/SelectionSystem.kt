package org.yunghegel.salient.editor.plugins.selection.systems

import com.badlogic.ashley.core.Entity
import org.yunghegel.salient.editor.plugins.BaseSystem

class SelectionSystem : BaseSystem(0){

    var selection: Entity = NoSelection


    object NoSelection : Entity()

}