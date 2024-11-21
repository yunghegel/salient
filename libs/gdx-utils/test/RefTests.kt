import mobx.core.computed
import mobx.core.spy
import org.yunghegel.gdx.utils.ext.*
import kotlin.test.Test

class RefTests {

    var ref :String by ref("Hello")


    @Test fun ref_test() {
        ref.watcheffects { new -> println("new: $new") }
        watch("ref") { value : String ->
            println("found ref change: $value")
        }



        ref = "World"

    }


}