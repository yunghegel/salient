import org.yunghegel.salient.engine.system.perf.IInvocationBehavior
import org.yunghegel.salient.engine.system.perf.InvocationBehavior
import org.yunghegel.salient.engine.system.perf.ProfilerInvocationHandler

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




}