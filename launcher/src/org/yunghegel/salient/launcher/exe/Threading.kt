package org.yunghegel.salient.launcher.exe

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import ktx.async.KtxAsync
import ktx.async.newSingleThreadAsyncContext

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
object threading {

    fun init () {
        KtxAsync.initiate()
    }

    val async = newSingleThreadAsyncContext()

    val nio = newSingleThreadContext("nio")

}

