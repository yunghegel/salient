package org.yunghegel.salient.launcher

import org.yunghegel.salient.editor.app.storage.Serializer
import org.yunghegel.salient.engine.api.model.ProjectHandle
import org.yunghegel.salient.engine.io.Paths

class StartupContext {

    val projectIndex: MutableMap<String,ProjectHandle> = mutableMapOf()

    fun recoverProjectIndex() : List<ProjectHandle> {
        val indexDir = Paths.PROJECTS_INDEX_DIR
        val projectHandles = mutableListOf<ProjectHandle>()
        indexDir.handle.list().filter { file ->
         !file.isDirectory && file.extension()=="index"
        }.forEach { handle ->
            val projectHandle = Serializer.yaml.decodeFromString(ProjectHandle.serializer(),handle.readString())
            projectHandles.add(projectHandle)
        }
        return projectHandles
    }



}