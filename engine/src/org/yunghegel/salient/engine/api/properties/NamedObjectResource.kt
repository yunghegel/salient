package org.yunghegel.salient.engine.api.properties

import org.yunghegel.gdx.utils.data.ID
import org.yunghegel.gdx.utils.data.Named
import org.yunghegel.salient.engine.api.Identifiable

interface NamedObjectResource : Resource, Identifiable {

    val exists: Boolean get() = file.exists

}