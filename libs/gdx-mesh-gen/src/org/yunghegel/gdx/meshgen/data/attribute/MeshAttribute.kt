package org.yunghegel.gdx.meshgen.data.attribute

import org.yunghegel.gdx.meshgen.data.base.StructuredMesh

interface MeshAttribute<M: StructuredMesh<*,*,*>,Data>  {

    fun get(mesh:M) : Data

    fun set(mesh:M, data:Data)

    fun clear(mesh:M)

    fun copy(mesh:M,other:M)



}