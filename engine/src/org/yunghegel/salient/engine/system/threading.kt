package org.yunghegel.salient.engine.system

import com.badlogic.gdx.Input.Keys.F
import kotlinx.coroutines.*
import ktx.async.KtxAsync
import ktx.async.newSingleThreadAsyncContext


@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
object threading {

    private var loaded = false

    fun init () {
        KtxAsync.initiate()
        loaded = true
    }

    val async = newSingleThreadAsyncContext()
        get () = if (loaded) field else throw IllegalStateException("Threading not initialized")

    fun doAsync (block: suspend ()->Unit) {
        KtxAsync.launch(async) {
            block()
        }
    }

    fun <T> await(block: suspend ()->T?, callback: (T?)->Unit) :T =runBlocking {
        val result = withContext(async) {
            block()
        }
        callback(result)
        result!!
    }



}

