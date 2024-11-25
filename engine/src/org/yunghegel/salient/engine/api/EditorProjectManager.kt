package org.yunghegel.salient.engine.api

import com.badlogic.gdx.files.FileHandle
import kotlinx.serialization.Serializable
import org.yunghegel.gdx.utils.ext.withExtension
import org.yunghegel.salient.engine.api.model.ProjectHandle
import org.yunghegel.salient.engine.api.project.EditorProject
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.system.file.Filepath
import org.yunghegel.salient.engine.system.file.Filepath.Companion.pathOf
import org.yunghegel.salient.engine.system.file.Paths


abstract class EditorProjectManager<P:EditorProject<P,S>,S:EditorScene> : Default<P> {

    private val index : MutableList<ProjectHandle> = mutableListOf()

    abstract var projectDir : FileHandle

    abstract var currentProject : P?

    abstract fun loadProject(file: Filepath, makeCurrent: Boolean = true): P

    abstract fun saveProject(project: P)

    abstract fun createNew(name: String) : P

    abstract fun initialize(project: P)

    fun createHandle(name: String) : ProjectHandle {
        val handle =  ProjectHandle(name, Paths.PROJECTS_DIR.child(name.withExtension("salient")))
        index.add(handle)
        return handle
    }








}