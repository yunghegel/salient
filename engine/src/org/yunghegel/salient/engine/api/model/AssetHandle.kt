package org.yunghegel.salient.engine.api.model

import com.badlogic.gdx.assets.AssetDescriptor
import org.yunghegel.salient.engine.api.ID
import org.yunghegel.salient.engine.api.Resource
import org.yunghegel.salient.engine.sys.Filepath

class AssetModel(path: String) : Resource,ID {

    override val uuid: String = generate()

    override val path: Filepath = Filepath(path)

    override val name: String = path

}