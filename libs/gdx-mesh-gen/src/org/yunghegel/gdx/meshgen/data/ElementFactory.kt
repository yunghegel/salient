package org.yunghegel.gdx.meshgen.data

import org.yunghegel.gdx.meshgen.data.base.Element

interface ElementFactory<E : Element> {

        fun create(index: Int): E

}