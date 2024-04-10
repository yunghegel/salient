package org.yunghegel.salient.engine.api

import org.yunghegel.salient.engine.api.model.ProjectHandle
import org.yunghegel.salient.engine.api.project.EditorProject
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.io.Filepath

interface EditorProjectManager<P:EditorProject<P,S>,S:EditorScene> {

    var currentProject : P?

    fun loadProject(file : Filepath, setCurrent:Boolean) : P

    fun saveProject(project: P)

    fun createNew(name: String) : ProjectHandle

    fun initialize(project: P)





}