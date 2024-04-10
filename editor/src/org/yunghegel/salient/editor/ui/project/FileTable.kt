package org.yunghegel.salient.editor.ui.project

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import org.yunghegel.salient.engine.ui.scene2d.SImage
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.scene2d.STextButton
import org.yunghegel.salient.engine.io.FileType
import org.yunghegel.salient.engine.io.info
import org.yunghegel.salient.engine.ui.Icons
import org.yunghegel.salient.engine.ui.scene2d.*

class FileTable(val file: FileHandle) : STable() {

    val icon : TextureRegionDrawable
    val name : SLabel

    val button : STextButton

    val fileType : FileType = FileType.parse(file.extension())

    init {
        println(file.extension())
        touchable = Touchable.enabled
        button= STextButton(file.name(),"empty")
        if (file.isDirectory) {
            icon   = Icons.folderIcon

        } else {
            icon  = when(file.extension()){

                "salient" -> Icons.projectFile
                "mat" -> Icons.materialFile
                "png" -> Icons.textureFile
                "scene" -> Icons.sceneFile
                "obj" -> Icons.modelFile
                "fbx" -> Icons.modelFile
                "gltf" -> Icons.modelFile
                "glb" -> Icons.modelFile
                else -> Icons.fileIcon
            }
            info("Discovered file of type ${fileType.icon.loc}")

        }
        add(SImage(icon)).size(16f).padRight(4f)
        add(button).growX()
        name = SLabel(file.name())
    }


}