import kotlinx.serialization.Serializable
import org.yunghegel.salient.engine.helpers.encode
import org.yunghegel.salient.engine.helpers.from
import org.yunghegel.salient.engine.helpers.serialize
import kotlin.test.Test

class RegTest {

    @Serializable
    data class TestData(val name:String, val age:Int)

    @Test fun test_encoding() {
        val data = TestData("John", 23)
        val encoded = data.serialize()
        println(encoded.data)
    }
    @Test fun test_save_infix() {
        val data = TestData("John", 23)
        val encoded = encode(data)
        encoded to "test.json"
    }

    @Test fun test_from_infix() {
        val data = TestData("John", 23)
        val encoded = encode(data)
        val decoded = TestData::class from "test.json"
        println(decoded)
    }
}