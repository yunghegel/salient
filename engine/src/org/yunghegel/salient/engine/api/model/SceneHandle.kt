package org.yunghegel.salient.engine.api.model

import org.yunghegel.salient.engine.api.ID
import org.yunghegel.salient.engine.api.Resource
import org.yunghegel.salient.engine.sys.Filepath

class SceneModel(name: String, path:String) : ID,Resource {

    override val uuid: String = generate()

    override val path: Filepath = Filepath(path)

    override val name: String = name

}