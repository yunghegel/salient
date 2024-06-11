package types

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.headless.HeadlessApplication
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration

open class HeadlessTest(name: String = "HeadlessTest") : BaseTest(name) {

    var config = HeadlessApplicationConfiguration()

    override var execCreate : ()->Unit = {}

    override var execRender: () -> Unit = {}

    var autoExit = true

    override fun create() {
        execCreate()
        if(autoExit) {
            exit()
        }
    }

    fun run() {
        HeadlessApplication(this, config)
    }

    fun exit() {
        Gdx.app.exit()
    }

}

fun headlessTest(autoexit: Boolean = true, run : ()->Unit) {
    val app = object : HeadlessTest() {

        init {
            autoExit = autoexit
        }

        override fun create() {
            execCreate()
            if (autoExit) {
                exit()
            }
        }

    }.run()
}

