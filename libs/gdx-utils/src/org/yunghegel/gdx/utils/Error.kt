package org.yunghegel.gdx.utils

class Error(message: String, val executeOnFailure: (() -> Unit)? = null) : Exception(message) {

    fun execute() {
        executeOnFailure?.invoke()
    }

    companion object {
        fun throwIf(condition: Boolean, message: String, executeOnFailure: (() -> Unit)? = null) {
            if (condition) {
                throw Error(message, executeOnFailure)
            }
        }
    }

}

class TryCatchBuilder<T> {

    private var tryBlock: (() -> T)? = null
    private var catchBlock: ((Throwable) -> T)? = null

    fun attempt(block: () -> T) {
        tryBlock = block
    }

    fun handle(block: (Throwable) -> T) {
        catchBlock = block
    }

    fun build(): T {
        return try {
            tryBlock?.invoke() ?: throw Error("Try block is not defined")
        } catch (e: Throwable) {
            catchBlock?.invoke(e) ?: throw e
        }
    }

}

fun <T> handler(block: TryCatchBuilder<T>.() -> Unit): T {
    val builder = TryCatchBuilder<T>()
    builder.block()
    return builder.build()
}

