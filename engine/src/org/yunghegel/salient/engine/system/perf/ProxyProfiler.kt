package org.yunghegel.salient.engine.system.perf

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.system.measureTimeMillis

class ProfilerInvocationHandler<T>(private val target: T) : InvocationHandler {

    private val methodCallCounts = mutableMapOf<String, Int>()
    private val methodExecutionTimes = mutableMapOf<String, Long>()

    override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any? {
        val methodName = method.name

        // Increase the method call count
        methodCallCounts[methodName] = methodCallCounts.getOrDefault(methodName, 0) + 1

        // Measure execution time
        val result: Any?
        val timeTaken = measureTimeMillis {
            result = method.invoke(target, *(args ?: arrayOfNulls<Any>(0)))
        }

        // Add the execution time
        methodExecutionTimes[methodName] = methodExecutionTimes.getOrDefault(methodName, 0L) + timeTaken

        return result
    }

    fun printProfilingInfo() {
        println("Profiling Information:")
        for (methodName in methodCallCounts.keys) {
            val count = methodCallCounts[methodName] ?: 0
            val totalTime = methodExecutionTimes[methodName] ?: 0L
            val averageTime = if (count > 0) totalTime / count else 0L
            println("Method: $methodName, Calls: $count, Total Time: ${totalTime}ms, Average Time: ${averageTime}ms")
        }
    }
}
inline fun <reified T> createProxy(target: T,interfaceType: Class<Any>): T {
    return Proxy.newProxyInstance(
        T::class.java.classLoader,
        arrayOf(interfaceType),
        ProfilerInvocationHandler(target)
    ) as T
}


