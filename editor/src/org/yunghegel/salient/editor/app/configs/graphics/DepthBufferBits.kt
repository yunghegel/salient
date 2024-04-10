package org.yunghegel.salient.editor.app.configs.graphics

enum class DepthBufferBits(val value: Int) {
    _16(16), _24(24), _32(32);

    override fun toString(): String {
        return name.substring(1)
    }
}
