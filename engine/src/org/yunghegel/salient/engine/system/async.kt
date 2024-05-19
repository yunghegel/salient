package org.yunghegel.salient.engine.system

import kotlinx.coroutines.*
import ktx.async.KtxAsync
import ktx.async.newAsyncContext
import ktx.async.newSingleThreadAsyncContext


@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
object async {

    private var loaded = false

    var threadcount = 2
        set (value) {
            field = value
            multithreaded = newAsyncContext(value, "multithreaded")
        }

    fun init () {
        KtxAsync.initiate()
        loaded = true
    }

    infix fun use(action: KtxAsync.()->Unit) {
        KtxAsync.action()
    }

    infix fun (suspend ()->Unit).use(threads:Int) {
        if (threads == 1) {
            KtxAsync.launch(singlethreaded) {
                this@use()
            }
        } else {
            threadcount = threads
            KtxAsync.launch(multithreaded) {
                this@use()
            }
        }
    }



    val singlethreaded = newSingleThreadAsyncContext()
        get () = if (loaded) field else throw IllegalStateException("Threading not initialized")

    var multithreaded = newAsyncContext(threadcount, "multithreaded")

    fun doAsync (block: suspend ()->Unit) {
        KtxAsync.launch(singlethreaded) {
            block()
        }
    }

    fun <T> await(block: suspend ()->T?, callback: (T?)->Unit) :T =runBlocking {
        val result = withContext(singlethreaded) {
            block()
        }
        callback(result)
        result!!
    }



}

