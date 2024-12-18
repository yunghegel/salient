package org.yunghegel.salient.engine.graphics.debug

const val RENDER = 0
const val DEBUG = 1
const val USES_SPRITE_BATCH = 2
const val USES_SHAPE_RENDERER = 4
const val USES_MODEL_BATCH = 8
const val IS_3D = 16
const val AFTER_DEPTH = 32

enum class DebugMask {
    RENDER,
    DEBUG,
    USES_SPRITE_BATCH,
    USES_SHAPE_RENDERER,
    USES_MODEL_BATCH,
    IS_3D,
    AFTER_DEPTH
}
