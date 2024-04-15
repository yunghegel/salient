package org.yunghegel.salient.engine.api

import org.yunghegel.salient.engine.system.file.Filepath

interface Saveable {

    fun save(path: Filepath)

}