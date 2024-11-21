package org.yunghegel.salient.engine.api.properties

import org.yunghegel.gdx.utils.data.ID
import org.yunghegel.gdx.utils.data.Named

interface NamedObjectResource : Resource, Named, ID {

    val exists: Boolean get() = file.exists

}