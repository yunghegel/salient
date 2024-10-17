import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.yunghegel.gdx.utils.KClassSerializer
import kotlin.reflect.KClass
import kotlin.test.Test

class SerializerTests {

    val kclass = String::class

    @Test fun testKClassSerializer() {
        val serializer = KClassSerializer
        val kclass = SerializerTests::class
        val encoded = serializer.encodeToString(kclass)

    }

}

@Serializable
class TestClass() {

    @Serializable(with = KClassSerializer::class)
    val kclass: KClass<*> = String::class
}