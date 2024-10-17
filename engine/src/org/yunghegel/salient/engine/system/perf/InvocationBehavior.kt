package org.yunghegel.salient.engine.system.perf

interface IInvocationBehavior {
    fun invoke()
    fun reset()
    fun getTopMethodCalls(max: Int): Int
    fun getCalls(method: String): Int
}

class InvocationBehavior : IInvocationBehavior {
    private val byCallsDesc: Comparator<String> =
        Comparator { o1, o2 -> Integer.compare(calls[o2] ?: 0, calls[o1] ?: 0) }

    override fun invoke() {
        val method = Thread.currentThread().stackTrace[2].methodName
        calls[method] = (calls[method] ?: 0) + 1
    }

    override fun reset() {
        calls.clear()
    }

    override fun getTopMethodCalls(max: Int): Int {
        val remaining = (calls.size - max).coerceAtLeast(0)
        return remaining
    }

    override fun getCalls(method: String): Int {
        return calls[method] ?: 0
    }

    companion object {
        val calls = mutableMapOf<String, Int>()
    }

}