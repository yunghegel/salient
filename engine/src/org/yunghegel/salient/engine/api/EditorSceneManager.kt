package org.yunghegel.salient.engine.api

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import org.yunghegel.gdx.utils.ext.dir
import org.yunghegel.gdx.utils.ext.withExtension
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.api.project.EditorProject
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.system.file.Filepath

abstract class EditorSceneManager<T:EditorScene> {

    val indices : MutableList<SceneHandle> = mutableListOf()

    abstract fun createNew(name:String) : T

    abstract fun loadScene(file: Filepath, makeCurrent: Boolean) : T

    abstract fun initialize(scene: T,makeCurrent:Boolean)

    abstract fun saveScene(scene: T)

    fun createHandle(name: String, project : EditorProject<*,T>) : SceneHandle {
        val filepath = project.folder.dir("name").child(name.withExtension("scene"))
        val handle = SceneHandle(name,Filepath(filepath))
        indices.add(handle)
        return handle
    }

}