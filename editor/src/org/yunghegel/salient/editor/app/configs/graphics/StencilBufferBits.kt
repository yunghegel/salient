package org.yunghegel.salient.modules.graphics.shared.config

enum class StencilBufferBits(val value: Int) {
    _0(0), _8(8);

    override fun toString(): String {
        return name.substring(1)
    }
}
