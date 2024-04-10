package org.yunghegel.salient.editor.plugins

import org.yunghegel.salient.editor.tool.Tool
import org.yunghegel.salient.engine.api.Initializable

interface Plugin : Initializable {

    val systems : List<BaseSystem>

    val tools : List<Tool>



}