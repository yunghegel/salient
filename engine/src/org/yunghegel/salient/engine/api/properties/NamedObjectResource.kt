package org.yunghegel.salient.engine.api.properties

import org.yunghegel.gdx.utils.data.ID
import org.yunghegel.gdx.utils.data.Named
import kotlin.io.path.exists

interface NamedObjectResource : Resource, Named, ID {

    val exists: Boolean get() = path.exists

}