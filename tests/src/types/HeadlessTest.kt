package types

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.headless.HeadlessApplication
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration

open class HeadlessTest : ApplicationAdapter() {

    val config = HeadlessApplicationConfiguration()

     var exec : ()->Unit = {}

    var autoExit = true

    override fun create() {
        exec()
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
            exec()
            if (autoExit) {
                exit()
            }
        }

    }.run()
}

