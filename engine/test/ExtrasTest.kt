import org.yunghegel.salient.engine.api.asset.props
import org.yunghegel.salient.engine.helpers.encodestring
import org.yunghegel.salient.engine.helpers.serialize
import kotlin.test.Test

class ExtrasTest {

    @Test fun `test extras`() {
        val extras = props("test" to "test_value")
        val serialized = encodestring(extras,false)
        println(serialized)
    }

}