import org.yunghegel.gdx.utils.ext.Observable
import org.yunghegel.gdx.utils.ext.observable
import kotlin.reflect.jvm.isAccessible
import kotlin.test.Test
import kotlin.test.asserter

class DelegatesTest {

    var test by observable(0) { old, new -> println("old: $old, new: $new") }


    @Test fun observable_delegate_test() {
        test = 2
        test = 3
    }

}