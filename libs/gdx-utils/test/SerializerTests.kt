import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.yunghegel.gdx.utils.KClassSerializer
import kotlin.reflect.KClass
import kotlin.test.Test

class SerializerTests {

    val kclass = String::class

    @Test fun testKClassSerializer() {

        val kclass = SerializerTests::class

    }

}

@Serializable
class TestClass() {


}