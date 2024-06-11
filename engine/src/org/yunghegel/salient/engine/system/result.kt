package org.yunghegel.salient.engine.system

import org.yunghegel.salient.engine.ui.widgets.notif.alert
import org.yunghegel.salient.engine.ui.widgets.notif.notify

class ResultBuilder<T>(private var errorMessage : String = "",private var successMessasge : String = "") {
    private var result: Result<T>? = null
    private var value: T? = null
        set (value) {
            field = value
            notify(successMessasge)
        }
    private var error: Throwable? = null
        set (error) {
            field = error
            alert(errorMessage)
        }

    fun value(success:String=successMessasge,init: () -> T) {
        successMessasge = success
        value = init()
    }

    fun error(failure:String=errorMessage,init: () -> Throwable) {
        errorMessage = failure
        error = init()
    }

    fun build(): Result<T> {
        result = if (value != null) {
            Result.success(value!!)
        } else {
            Result.failure(error!!)
        }
        return result!!
    }
}

inline fun <T> result(builderFunction: ResultBuilder<T>.() -> Unit): Result<T> {
    val builder = ResultBuilder<T>()
    builder.builderFunction()
    return builder.build()
}

fun Result<*>.ifSuccess(action: () -> Unit) : Boolean {
    if (isSuccess) action()
    return isSuccess
}

fun Result<*>.ifFailure(action: () -> Unit) : Boolean {
    if (isFailure) action()
    return isFailure
}