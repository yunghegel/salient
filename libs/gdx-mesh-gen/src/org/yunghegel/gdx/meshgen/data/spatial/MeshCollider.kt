package org.yunghegel.gdx.meshgen.data.spatial

import com.badlogic.gdx.math.Frustum
import com.badlogic.gdx.math.Octree
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.math.collision.Ray
import org.yunghegel.gdx.meshgen.data.base.Edge
import org.yunghegel.gdx.meshgen.data.base.Face
import org.yunghegel.gdx.meshgen.data.base.StructuredMesh
import org.yunghegel.gdx.meshgen.data.base.Vertex

class MeshCollider<V:Vertex,F: Face,E:Edge,M:StructuredMesh<V,F,E>> : Octree.Collider<M> {

    override fun intersects(nodeBounds: BoundingBox?, geometry: M): Boolean {
        TODO("Not yet implemented")
    }

    override fun intersects(frustum: Frustum?, geometry: M): Boolean {
        TODO("Not yet implemented")
    }

    override fun intersects(ray: Ray?, geometry: M): Float {
        TODO("Not yet implemented")
    }
}