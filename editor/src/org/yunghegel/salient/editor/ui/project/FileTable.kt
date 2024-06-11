package org.yunghegel.salient.editor.ui.project

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import org.yunghegel.salient.engine.system.file.FileType
import org.yunghegel.salient.engine.ui.Icons
import org.yunghegel.salient.engine.ui.scene2d.SImage
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.scene2d.STextButton
import org.yunghegel.salient.engine.ui.tree.TreeNode

class FileTable(val file: FileHandle) : STable() {


    val name : SLabel

    val button : STextButton

    val fileType : FileType = FileType.parse(file.extension())

    init {
        touchable = Touchable.enabled
        button= STextButton(file.name(),"empty")
        val icon : Drawable

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
        }
        name = SLabel(file.name())
        name.wrap = true
        name.setEllipsis(true)
        add(name).growX().left().minWidth(100f)
    }


}