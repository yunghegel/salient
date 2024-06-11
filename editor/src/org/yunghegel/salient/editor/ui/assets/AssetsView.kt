package org.yunghegel.salient.editor.ui.assets

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.utils.Align
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.gdx.utils.ext.padVertical
import org.yunghegel.gdx.utils.ext.type
import org.yunghegel.salient.editor.asset.AssetManager
import org.yunghegel.salient.engine.api.asset.type.AssetType
import org.yunghegel.salient.engine.events.lifecycle.filesDropped
import org.yunghegel.salient.engine.system.file.FileType
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.scene2d.SImage
import org.yunghegel.salient.engine.ui.scene2d.SList
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.widgets.notif.toast

class AssetsView: STable() {

    private val assetManager : AssetManager = inject()
    private val sceneAssetsList = SceneAssetsList(inject(),inject(),assetManager)
    private val projectAssetsList = ProjectAssetsList(inject(),assetManager,sceneAssetsList)


    init {
        pad(4f)
        align(Align.top)
        add(projectAssetsList).growX().padVertical(5f)
        row()
        add(sceneAssetsList).growX().padVertical(5f)
        filesDropped { arr ->
            toast(title = "Add the following assets to the scene?") {

                arr.forEach { item ->
                    val file = FileHandle(item)
                    val type = AssetType.fromFile(file)
                    row {
                        image(AssetType.iconFor(type ?: AssetType.Other)).padHorizontal(5f)
                        label(file.name()).padRight(3f)
                        option(item,true) {
                            result[item] = it
                        }

                    }
                    separator()
                }
                submit = {res ->
                    arr.forEach { println(res[it]) }
                }
            }

        }

    }





}