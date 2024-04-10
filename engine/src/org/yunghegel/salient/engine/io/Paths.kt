package org.yunghegel.salient.engine.io

import org.yunghegel.salient.engine.io.Filepath.Companion.pathOf

object Paths {

    val USER_HOME: String =  System.getProperty("user.home")

    val SALIENT_HOME : Filepath = "$USER_HOME/.salient".pathOf()

    val SALIENT_METAFILE : Filepath = "${SALIENT_HOME.path}/.salient.meta".pathOf()

    val PROJECTS_DIR : Filepath = "${SALIENT_HOME.path}/projects".pathOf()

    val PROJECTS_INDEX_DIR : Filepath = "${PROJECTS_DIR.path}/.project-index".pathOf()

    val PROJECT_DIR_FOR : (name: String) -> Filepath = { name -> "${PROJECTS_DIR.path}/$name".pathOf() }

    val PROJECT_FILE_FOR : (name: String) -> Filepath = { name -> "${PROJECTS_DIR.path}/$name/$name.salient".pathOf() }

    val PROJECT_SCOPE_ASSETS_DIR_FOR : (name: String) -> Filepath = { name -> "${PROJECTS_DIR.path}/$name/.asset-index".pathOf() }

    val SCENE_DIR_FOR : (name: String) -> Filepath = { name -> "${PROJECTS_DIR.path}/$name/scenes".pathOf() }

    val SCENE_INDEX_DIR_FOR : (name: String) -> Filepath = { name -> "${PROJECTS_DIR.path}/$name/scenes/.scene-index".pathOf() }

    val SCENE_INDEX_FILEPATH_FOR : (projectName: String,sceneName:String ) -> Filepath = { projectName, sceneName -> "${PROJECTS_DIR.path}/$projectName/scenes/.scene-index/$sceneName.index".pathOf() }

    val SCENE_FILE_FOR : (projectName: String,sceneName:String ) -> Filepath = { projectName, sceneName -> "${PROJECTS_DIR.path}/$projectName/scenes/$sceneName.scene".pathOf() }



    val SCENE_SCOPE_ASSETS_DIR_FOR :(projectName: String,sceneName:String ) -> Filepath = { projectName, sceneName -> "${PROJECTS_DIR.path}/$projectName/scenes/$sceneName/assets".pathOf() }



}