import org.yunghegel.salient.engine.system.perf.IInvocationBehavior
import org.yunghegel.salient.engine.system.perf.InvocationBehavior
import org.yunghegel.salient.engine.system.perf.ProxyProfiler
import java.lang.reflect.Method
import kotlin.test.Test

class ProxyProfilerTest {

    interface ProxyObject {
        fun method1()
        fun method2()
    }

    class InvokedObject : ProxyObject{
        override fun method1() {
            println("Method 1")
        }

        override fun method2() {
            println("Method 2")
        }
    }



    @Test
    fun testProxyProfiler() {
        val proxyProfiler = ProxyProfiler()
        val proxy = ProxyProfiler.createProfiledType(InvokedObject::class.java, InvocationBehavior())
        proxy.method1()
        proxy.method2()
        proxyProfiler.resetMethods()
        proxy.method1()
        proxy.method2()
        val methods = com.badlogic.gdx.utils.Array<Method?>()
        val remaining = proxyProfiler.getTopMethodCalls(methods, 1)
        println("Remaining: $remaining")
        for (method in methods) {
            println(method)
        }


    }
}