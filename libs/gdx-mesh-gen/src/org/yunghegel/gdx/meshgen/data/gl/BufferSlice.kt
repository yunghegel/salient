package org.yunghegel.gdx.meshgen.data.gl

import org.yunghegel.gdx.meshgen.data.MeshAttributes
import org.yunghegel.gdx.meshgen.data.MeshProperties
import org.yunghegel.gdx.meshgen.data.base.Element
import org.yunghegel.gdx.meshgen.data.ifs.IVertex

interface BufferSlice<E:Element, Primitive> {

    fun createSlice(props: MeshAttributes) : Array<Primitive>

}