package org.yunghegel.gdx.util

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

open class App : ApplicationAdapter() {

    companion object : Lwjgl3ApplicationConfiguration() {
        fun create(conf: Lwjgl3ApplicationConfiguration.()->Unit = {}) = Lwjgl3ApplicationConfiguration().apply {
            setTitle("Test Application")
            setWindowedMode(800, 600)

            conf()
        }
    }

    fun run(conf: Lwjgl3ApplicationConfiguration.()->Unit = {}) {
        Lwjgl3Application(this, create(conf))
    }

}