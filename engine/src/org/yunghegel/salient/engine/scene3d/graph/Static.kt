package org.yunghegel.salient.engine.scene3d.graph

import com.badlogic.gdx.math.Matrix4
import org.checkerframework.checker.units.qual.K
import org.yunghegel.salient.engine.api.properties.ObjectPayload

abstract class Static<Parent,Child,Object>(name:String,val parentObject :Parent ) : Node<Static<Parent,Child,Object>, Parent> (name) , ObjectPayload<Object> {






}
