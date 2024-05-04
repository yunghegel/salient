package org.yunghegel.gdx.meshgen.math.operators

import org.yunghegel.gdx.meshgen.data.base.Element

interface ElementOperation<E: Element,Computed> {

    fun modify(element: E)

    fun calculate(element: E) : Computed

}