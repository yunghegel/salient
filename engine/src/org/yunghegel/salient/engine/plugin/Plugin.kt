package org.yunghegel.salient.engine.plugin

import ktx.inject.Context
import org.yunghegel.salient.engine.api.ID
import org.yunghegel.salient.engine.api.Initializable

interface Plugin : Initializable, ID {

    val afterEval : MutableList<()->Unit>

    val beforeEval : MutableList<()->Unit>

    val registry : Context.() -> Unit

}