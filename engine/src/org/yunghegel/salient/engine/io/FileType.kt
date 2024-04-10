package org.yunghegel.salient.engine.sys

import org.yunghegel.salient.engine.ui.Icons

enum class FileType(val icon: Icons,vararg val extensions: String) {

    META(Icons.CONFIG_FILE,"meta"),
    PROJECT(Icons.PROJECT_FILE,"salient"),
    SCENE(Icons.SCENE_FILE,"scene"),
    ASSET_INDEX(Icons.TEXT_FILE,"asset"),
    SCENE_INDEX(Icons.TEXT_FILE,"index"),
    PROJECT_INDEX(Icons.TEXT_FILE,"index"),
    LOG(Icons.TEXT_FILE,"log"),
    CONFIG(Icons.CONFIG_FILE,"config"),
    MODEL(Icons.MODEL_FILE,"gltf","glb","obj","fbx","g3d"),
    TEXTURE(Icons.TEXTURE_FILE,"png","jpg","jpeg","bmp","gif","tga","dds","ktx","pvr","astc","webp"),
    MATERIAL(Icons.MATERIAL_FILE,"mat"),
    OTHER(Icons.FILE);

    companion object {
        fun parse(ext: String) : FileType {
            for (type in entries) {
                if (ext in type.extensions) return type
            }
            return OTHER
        }

        fun isAsset(type: FileType) : Boolean {
            return (type == ASSET_INDEX || type == MODEL || type == TEXTURE || type == MATERIAL)
        }
    }

}