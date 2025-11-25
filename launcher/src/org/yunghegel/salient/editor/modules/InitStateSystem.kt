package org.yunghegel.salient.editor.modules

import com.badlogic.ashley.core.Component
import org.yunghegel.salient.editor.plugins.rendering.State

//class InitStateSystem : StateSystem(State.INIT)
//class PrepareSceneSystem : StateSystem(State.PREPARE_SCENE)
//class BeforeDepthPassSystem : StateSystem(State.BEFORE_DEPTH_PASS)
//class DepthPassSystem : StateSystem(State.DEPTH_PASS)
//class BeforeColorPassSystem : StateSystem(State.BEFORE_COLOR_PASS)
//class ColorPassSystem : StateSystem(State.COLOR_PASS)
//class AfterColorPassSystem : StateSystem(State.AFTER_COLOR_PASS)
//class BeforeUIPassSystem : StateSystem(State.BEFORE_UI_PASS)
//class UIPassSystem : StateSystem(State.UI_PASS)
//class OverlayPassSystem : StateSystem(State.OVERLAY_PASS)


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