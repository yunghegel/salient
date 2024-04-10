package org.yunghegel.salient.engine.scene3d.component

import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.utils.Array
import ktx.collections.GdxArray
import org.yunghegel.salient.engine.ecs.EntityComponent

class MeshComponent(mesh:GdxArray<Mesh>) : EntityComponent<Array<Mesh>>(null, mesh){
}