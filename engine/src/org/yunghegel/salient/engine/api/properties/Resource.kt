package org.yunghegel.salient.engine.api.properties

import org.yunghegel.salient.engine.helpers.encodestring
import org.yunghegel.salient.engine.system.file.Filepath

interface Resource {

    val file : Filepath

    fun save() {
        org.yunghegel.salient.engine.helpers.save(file.path,{  encodestring(this) } )
    }





}