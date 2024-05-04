package org.yunghegel.gdx.meshgen.math.operators

import org.yunghegel.gdx.meshgen.data.base.Element

abstract class AbstractOperation<E: Element,Computed>(var element: E) : ElementOperation<E,Computed> {
}