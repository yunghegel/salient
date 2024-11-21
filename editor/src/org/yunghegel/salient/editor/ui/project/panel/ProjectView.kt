package org.yunghegel.salient.editor.ui.project.panel

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane
import org.yunghegel.gdx.utils.ext.TOP_LEFT
import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.editor.ui.project.FileTree
import org.yunghegel.salient.engine.system.file.Paths
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.scene2d.STable

class ProjectView : STable() {

    val proj : Project = inject()

    val fileSystem : FileTree = FileTree(Paths.PROJECT_DIR_FOR(proj.name).handle)
    val pane : ScrollPane

    var top = STable()
    var bottom = STable()

    var split = SplitPane(top,bottom,true,skin )

    init {
        align(TOP_LEFT)
        val fsContainer = object : STable() {
            override fun getPrefHeight(): Float {
                return fileSystem.getPrefHeight()
            }
        }
        pane = ScrollPane(fileSystem)
        pane.setScrollingDisabled(true,false)
//        top.add(pane).grow()
        add(pane).pad(2f).grow()
        setBackground("dark-gray")

    }

}