package org.yunghegel.salient.launcher

import com.badlogic.gdx.Input.Keys.F
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import ktx.async.KtxAsync
import ktx.async.newSingleThreadAsyncContext
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.thread
@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
object threading {

    fun init () {
        KtxAsync.initiate()
    }

    val async = newSingleThreadAsyncContext()

    val nio = newSingleThreadContext("nio")

}

