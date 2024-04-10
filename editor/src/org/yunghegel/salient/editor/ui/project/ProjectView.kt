package org.yunghegel.salient.editor.ui.project

import org.yunghegel.gdx.utils.ext.TOP
import org.yunghegel.gdx.utils.ext.TOP_LEFT
import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.engine.io.Paths
import org.yunghegel.salient.engine.io.inject
import org.yunghegel.salient.engine.ui.scene2d.STable

class ProjectView : STable() {

    val proj : Project = inject()

    val fileSystem : FileSystem = FileSystem(Paths.PROJECT_DIR_FOR(proj.name).handle)

    init {
        align(TOP_LEFT)
        add(fileSystem).grow()
        setBackground("dark-gray")

    }

}