package org.yunghegel.salient.editor.cmd

import org.yunghegel.gdx.cli.CommandSet
import org.yunghegel.gdx.cli.arg.Cmd
import org.yunghegel.gdx.cli.arg.Namespace
import org.yunghegel.gdx.cli.util.StdOut
import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.editor.project.ProjectManager
import org.yunghegel.salient.engine.system.inject

@Namespace("project")
class ProjectContext : CommandSet<Project> {

    override fun injectDependency(): Project {
        return inject()
    }

    @Cmd("listScenes","List all scenes in the project")
    fun listScenes() {
         StdOut.writeLn(depedency?.scenes?.map{it.handle}!!.joinToString("\n"))

    }

    @Cmd("listAssets","List all assets in the current scene")
    fun listAssets() {
        StdOut.writeLn(depedency!!.currentScene!!.assets.map { it.handle.toString() }.joinToString("\n"))
    }

    @Cmd("save","Save the project")
    fun save() {
        val projMan = inject<ProjectManager>()
        projMan.saveProject(depedency!!)
    }



}