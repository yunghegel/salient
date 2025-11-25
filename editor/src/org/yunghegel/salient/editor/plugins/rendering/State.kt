package org.yunghegel.salient.editor.plugins.rendering

import com.badlogic.ashley.core.Component

/**
 * Enumeration of necessary render states that must occur in a specific order; at particular moments we have to access the OpenGL context
 * and modify it to support the rendering of things in a certain order
 */
enum class State : Component {
    INIT,
    UI_LOGIC,
    PREPARE_SCENE,
    BEFORE_DEPTH_PASS,
    DEPTH_PASS,
    BEFORE_COLOR_PASS,
    COLOR_PASS,
    AFTER_COLOR_PASS,
    BEFORE_UI_PASS,
    UI_PASS,
    OVERLAY_PASS;
}