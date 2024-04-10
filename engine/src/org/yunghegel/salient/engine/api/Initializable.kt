package org.yunghegel.salient.engine

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity

interface Initializable {

    fun init(engine:Engine)

}