package org.yunghegel.salient.editor.input

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.PerspectiveCamera
import org.yunghegel.salient.engine.api.tool.InputTool

class PanTool : InputTool("Camera pan tool") {
    override var camera : PerspectiveCamera = PerspectiveCamera()
}