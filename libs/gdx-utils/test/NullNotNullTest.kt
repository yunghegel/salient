import org.yunghegel.gdx.utils.ext.nullOrNotNull
import kotlin.test.Test

class NullNotNullTest {

    @Test fun testNullNotNull() {
        val a: String? = null
        val b: String? = "b"
        val c: String = "c"
        val d: String = "d"

        a.nullOrNotNull {
            isNull { println("a is null") }
            notNull { println("a is not null") }
        }

        c.nullOrNotNull {
            isNull { println("c is null") }
            notNull { println("c is not null") }
        }


    }

}