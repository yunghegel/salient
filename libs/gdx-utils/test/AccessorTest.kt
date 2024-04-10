import org.yunghegel.gdx.utils.reflection.FieldAccessor
import kotlin.test.Test

class AccessorTest {

    data class Values(val a: Int, val b: String)

    @Test
    fun test() {
        val accessors = mutableListOf<FieldAccessor>()
        val values = Values(1, "2")
        values::class.java.declaredFields.forEach {
            accessors.add(FieldAccessor(values, it))
        }

        accessors.forEach {
            println(it.get())
        }

        accessors[0].set(3)

        println(accessors[0].get())

        accessors[1].set("4")

        println(accessors[1].get())


    }
}