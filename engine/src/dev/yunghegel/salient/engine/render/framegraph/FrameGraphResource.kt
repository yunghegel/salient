package dev.yunghegel.salient.engine.render.framegraph

@JvmInline
value class FrameGraphResource(val name: String) {
    companion object {
        val BACKBUFFER = FrameGraphResource("backbuffer")
    }
}