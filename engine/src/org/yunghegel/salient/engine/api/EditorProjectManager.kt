package org.yunghegel.salient.engine.api

import org.yunghegel.salient.engine.api.model.ProjectHandle
import org.yunghegel.salient.engine.api.project.EditorProject
import org.yunghegel.salient.engine.sys.Filepath

interface ProjectManager {

    var currentProject : ProjectHandle

    fun loadProject(file : Filepath) : EditorProject

    fun saveProject(project: EditorProject)

    fun createNew(name: String) : ProjectHandle

    fun initialize(project: EditorProject)



}