package org.yunghegel.salient.editor.input

import org.yunghegel.salient.engine.plugin.module
import org.yunghegel.salient.editor.render.systems.SelectionSystem
import org.yunghegel.salient.engine.input.Input
import org.yunghegel.salient.engine.ui.UI

val InputModule = module {

    tools {
        listOf()
    }

    systems {
        listOf(SelectionSystem())
    }

    provide {
//        singleton(EditorCamera())
    }

    finalize {
        Input.addProcessor(UI)
    }

}