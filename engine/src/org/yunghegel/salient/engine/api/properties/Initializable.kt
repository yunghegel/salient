package org.yunghegel.salient.engine.api.properties

import com.badlogic.ashley.core.Engine

interface Initializable {

    fun init(engine:Engine)

}