package org.yunghegel.salient.engine.scene3d.graph

import com.badlogic.gdx.utils.Array
import org.yunghegel.gdx.utils.data.ID
import org.yunghegel.gdx.utils.data.Named
import org.yunghegel.salient.engine.api.ecs.SpatialEntity

@Suppress("UNCHECKED_CAST")
abstract class BaseNode<T: GraphNode<T>>(override var name:String, uuid:String?=null, id:Int?=null ) : SpatialEntity(),

     GraphNode<T>, ID, Named {

    override val uuid: String =uuid ?: generateUUID()

    override val id: Int = id ?: generateID()

    internal val children : Array<T> = Array<T>()

    internal var parent : T? = null

    override fun addChild(child: T ) {
        children.add(child)
        child.setParent(this as T)
    }

    override fun removeChild(child: T) {
        children.removeValue(child, true)
        child.setParent(null)
    }

    override fun getChildren(): Array<T> {
        return children
    }

    override fun getParent(): T? {
        return parent
    }

    override fun setParent(parent: T?) {
        this.parent = parent
    }


}