package org.yunghegel.salient.editor.ui.project

import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.engine.events.project.onProjectLoaded
import org.yunghegel.salient.engine.io.inject
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable

class ProjectSnippet : STable() {

    val name: SLabel
    val path: SLabel
    val uuid: SLabel

    var proj : Project = inject()
        set(value) {
            field = value
            name.setText("Name: ${value.name}")
            path.setText("Path: ${value.path}")
            uuid.setText("UUID: ${value.uuid}")
        }



    init {
        onProjectLoaded { e ->
            this.proj = e.project as Project
        }
        name = SLabel("Name: ${proj.name}")
        path = SLabel("Path: ${proj.path}")
        uuid = SLabel("UUID: ${proj.uuid}")

        val inline = STable()
        inline.add(name).pad(5f)
        inline.add(path).pad(5f)

        add(inline).pad(5f).growX().row()
        add(uuid).pad(5f).growX()

    }

}