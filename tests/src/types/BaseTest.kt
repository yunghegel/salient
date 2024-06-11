package types

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
@OptIn(kotlin.ExperimentalStdlibApi::class)
abstract class BaseTest(override var name: String) : ApplicationAdapter(), Test {


}