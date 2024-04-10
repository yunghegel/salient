package org.yunghegel.salient.editor.app.configs.graphics

enum class StencilBufferBits(val value: Int) {
    _0(0), _8(8);

    override fun toString(): String {
        return name.substring(1)
    }
}
