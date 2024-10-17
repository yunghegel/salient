import com.badlogic.gdx.math.Vector3
import org.yunghegel.gdx.utils.ext.*
import kotlin.test.Test

class ReactivityTests {


    data class Data (var mutabledata: String = "Hello", var mutabledata2: Vector3 = Vector3(0f,0f,0f))


    private var state by ref( object {
        var valuef by ref(0f)
        var valueS by ref ("Hello")
        override fun toString(): String {
            return "Valuef: $valuef, ValueS: $valueS"
        }
    })


    val data = Data()

    var ref1 by ref(data)

    private var list by ref(object{var arr by ref(  mutableListOf(1,2,3,4,5,6,7,8,9,10) )})

    @Test
    fun ref_test_get_set() {
        watchEffect {
            println(data)
        }

        data.mutabledata = "Test Change"

        data.mutabledata2 = Vector3(1f,1f,1f)

    }

    @Test
    fun `ref reactivity test`() {
        println(data.mutabledata2)
        data.mutabledata = "Changed"
        println(data.mutabledata2)
    }

    @Test fun `watch property function`() {
        watchEffect { println(state.valuef) }
        watchEffect { println(state.valueS) }
        state.valuef = 1f
        state.valueS = " wWorld"

    }

    @Test fun `property access watcheffect`() {
        state.watcheffects { s ->
            println(s.toString())
        }
        state.valueS = "World"
        state.valuef = 1f
    }
    var price by ref(200)
    var quantity by ref(1000)
    val revenue by computed { price * quantity }

    @JvmInline
    value class Typed(val named: Any) {
        override fun toString(): String {
            return named.toString()
        }
    }

    var ty1 by ref(Typed("Type") )
    val ty2 by ref( Typed("typed2"))

    val name by computed {"$ty1 $ty2"}

    @Test fun `array ref effect reaction`() {
        watchEffect { println(ty1) }
        ty1 = Typed("Type Changed")
    }

}