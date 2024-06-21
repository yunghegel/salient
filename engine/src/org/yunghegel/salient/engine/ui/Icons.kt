package org.yunghegel.salient.engine.ui

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import org.yunghegel.salient.engine.ui.scene2d.SImage
import java.util.*

enum class Icons(val path: String) {

    FOLDER("default-folder"),
    FOLDER_COPY("default-folder-copy"),
    FOLDER_MOVE("default-folder-move"),
    FOLDER_OPEN("default-folder-open"),

    FILE("file_16x16"),
    TEXT_FILE("text_file"),
    PROJECT_FILE("project_file"),
    MATERIAL_FILE("material_file"),
    TEXTURE_FILE("texture_file"),
    SCENE_FILE("scene_file"),
    MODEL_FILE("model_file"),
    CONFIG_FILE("config_file"),

    PLANE("plane"),
    CONE("cone"),
    CUBE("cube"),
    CYLINDER("cylinder"),
    SPHERE("sphere"),
    TORUS("torus"),

    SPOT_LIGHT("spot-light"),
    POINT_LIGHT("point-light"),
    DIRECTIONAL_LIGHT("directional-light"),

    WINDOW_ICONIFY("window_iconify"),
    WINDOW_ICONIFY_OVER("window_iconify_over"),
    TERMINAL("terminal"),
    LOG_VIEWER("log_view");

    val loc : String by lazy { basepath + path + ext }

    val texture : Texture by lazy { Texture(loc) }
    val drawable : TextureRegionDrawable by lazy {  TextureRegionDrawable(texture) }
    val img : SImage by lazy {  SImage(drawable) }




    companion object {

        val folderIcon by lazy { TextureRegionDrawable(Texture("skin/icons/default-folder.png")) }
        val openFolderIcon by lazy { TextureRegionDrawable(Texture("skin/icons/default-fileopen.png")) }
        val moveFolderIcon by lazy { TextureRegionDrawable(Texture("skin/icons/default-folder-move.png")) }
        val fileIcon by lazy { TextureRegionDrawable(Texture("skin/icons/file_16x16.png")) }
        val projectFile by lazy { TextureRegionDrawable(Texture("skin/icons/project_file.png")) }
        val sceneFile by lazy { TextureRegionDrawable(Texture("skin/icons/scene_file.png")) }
        val materialFile by lazy { TextureRegionDrawable(Texture("skin/icons/material_file.png")) }
        val textureFile by lazy { TextureRegionDrawable(Texture("skin/icons/texture_file.png")) }
        val modelFile by lazy { TextureRegionDrawable(Texture("skin/icons/model_file.png")) }

        private const val basepath = "skin/icons/"
        private const val ext = ".png"


        fun parse(name: String) : Icons {
            return Icons.valueOf(name.uppercase(Locale.getDefault()))
        }

        operator fun get(name: String): TextureRegionDrawable {
            return parse(name).drawable
        }
    }
}

