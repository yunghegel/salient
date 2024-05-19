import org.yunghegel.salient.engine.system.store
import org.yunghegel.salient.engine.system.store_object
import kotlin.test.Test


class IndexingTests {

    val keys = store

    val test_prop = 100f

    @Test fun global_scoped() {
        store_object("global","Float",0f)
        store_object("global","String","string")

        val float = store_object("floats","Float",0f)
        val string = store_object("strings","String","string")



        println(keys.keys)

    }


}