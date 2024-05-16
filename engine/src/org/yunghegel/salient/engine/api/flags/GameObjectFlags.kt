package org.yunghegel.salient.engine.api.flags

import org.yunghegel.gdx.utils.data.Bitset

object GameObjectFlags : Bitset()  {

    init {
        register("SELECTED")
        register("RENDER")
        register("ALLOW_SELECTION")
        register("DRAW_WIREFRAME")
        register("DRAW_BOUNDS")
        register("DRAW_ORIGIN")
        register("DRAW_OUTLINE")
        register("LOCKED")
        register("DEBUG_ALL")
    }

    val SELECTED by registry
    val RENDER by registry
    val ALLOW_SELECTION by registry
    val DRAW_WIREFRAME by registry
    val DRAW_BOUNDS by registry
    val DRAW_ORIGIN by registry
    val LOCKED by registry
    val DEBUG_ALL by registry
    val DRAW_OUTLINE by registry

}

val SELECTED = GameObjectFlag.SELECTED
val RENDER = GameObjectFlag.RENDER
val ALLOW_SELECTION = GameObjectFlag.ALLOW_SELECTION
val DRAW_WIREFRAME = GameObjectFlag.DRAW_WIREFRAME
val DRAW_BOUNDS = GameObjectFlag.DRAW_BOUNDS
val DRAW_ORIGIN = GameObjectFlag.DRAW_ORIGIN
val LOCKED = GameObjectFlag.LOCKED
val DEBUG_ALL = GameObjectFlag.DEBUG_ALL
val DRAW_OUTLINE = GameObjectFlag.DRAW_OUTLINE
val HOVERED = GameObjectFlag.HOVERED

enum class GameObjectFlag {
    SELECTED,
    RENDER,
    ALLOW_SELECTION,
    DRAW_WIREFRAME,
    DRAW_BOUNDS,
    DRAW_ORIGIN,
    DRAW_OUTLINE,
    LOCKED,
    DEBUG_ALL,
    HOVERED
}