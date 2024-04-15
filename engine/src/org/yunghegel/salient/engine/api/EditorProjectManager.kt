package org.yunghegel.salient.engine.api

import org.yunghegel.salient.engine.api.project.EditorProject
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.system.file.Filepath

interface EditorProjectManager<P:EditorProject<P,S>,S:EditorScene> {

    var currentProject : P?

    fun loadProject(file : Filepath) : P

    fun saveProject(project: P)

    fun createNew(name: String) : P

    fun initialize(project: P)





}