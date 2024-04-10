package org.yunghegel.salient.editor.ui

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import org.yunghegel.salient.engine.ui.scene2d.SImage
import java.util.*
import kotlin.enums.enumEntries

enum class Icons(val path: String) {

    FOLDER("default-folder"),
    FOLDER_COPY("default-folder-copy"),
    FOLDER_MOVE("default-folder-move"),
    FOLDER_OPEN("default-folder-open"),

    FILE("generic_file"),
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
    DIRECTIONAL_LIGHT("directional-light");

    val loc : String by lazy { basepath + path + ext }

    val texture : Texture by lazy { Texture(loc) }
    val drawable : TextureRegionDrawable by lazy { TextureRegionDrawable(texture) }
    val img : SImage by lazy { SImage(drawable) }




    companion object {
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

