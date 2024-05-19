package org.yunghegel.salient.engine.scene3d.graph

import com.badlogic.gdx.math.Matrix4
import org.checkerframework.checker.units.qual.K
import org.yunghegel.salient.engine.api.properties.ObjectPayload

abstract class Static<Parent,Child>(name:String,val parentObject :Parent ) : Node<Static<Parent,Child>, Parent> (name) {

    val childObjects: MutableList<Child> = mutableListOf()

//    overiide to model relationshup





}
