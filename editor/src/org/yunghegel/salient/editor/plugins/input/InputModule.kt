package org.yunghegel.salient.editor.plugins.input

import org.yunghegel.salient.editor.input.Input
import org.yunghegel.salient.editor.plugins.module
import org.yunghegel.salient.editor.plugins.selection.systems.SelectionSystem
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