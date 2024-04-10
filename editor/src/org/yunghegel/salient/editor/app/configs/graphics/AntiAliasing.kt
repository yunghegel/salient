package org.yunghegel.salient.modules.graphics.shared.config

enum class AntiAliasing(val value: Int) {
    _0(0), _2(2), _4(4), _8(8), _16(16);

    override fun toString(): String {
        return name.substring(1)
    }
}
