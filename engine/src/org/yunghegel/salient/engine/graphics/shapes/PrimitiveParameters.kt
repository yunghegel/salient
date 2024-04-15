package org.yunghegel.salient.engine.graphics.shapes

interface ShapeParameters {
    val integerParams : MutableList<String>
    val floatParmas : MutableList<String>

    val setParams: ()->Unit

    fun int(name: String) {
        integerParams+=name
    }

    fun float(name: String) {
        floatParmas+=name
    }

}

