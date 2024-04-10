package org.yunghegel.salient.modules.graphics.shared.config

enum class DepthBufferBits(val value: Int) {
    _16(16), _24(24), _32(32);

    override fun toString(): String {
        return name.substring(1)
    }
}
