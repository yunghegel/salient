package org.yunghegel.salient.engine.api

import org.yunghegel.salient.engine.io.Filepath

interface Saveable {

    fun save(path: Filepath)

}