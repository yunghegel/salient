package org.yunghegel.salient.engine.scene3d.graph

import com.badlogic.ashley.core.Component
import com.google.common.base.Objects
import org.yunghegel.gdx.utils.data.ID
import org.yunghegel.salient.engine.api.ecs.BaseComponent
import org.yunghegel.salient.engine.ui.widgets.value.ReflectionBasedEditor.AccessorRegistry.name

abstract class ObjectNode<Object,T:ObjectNode<Object,T>>(name: String, val obj : Object) : Node<T,Object>(name) {


    abstract fun emitComponents(obj: Object) : List<BaseComponent>


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        if (!super.equals(other)) return false
        val that = other as ObjectNode<*,*>?
        return id == that!!.id
    }

    override fun hashCode(): Int {
        return Objects.hashCode(super.hashCode(), id)
    }

    override fun toString(): String {
        return "ObjectNode{" +
                "id=" + id +
                ", name='" + name + '\''.toString() +
                '}'.toString()
    }
}