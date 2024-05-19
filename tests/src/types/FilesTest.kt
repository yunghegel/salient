package types

import com.badlogic.gdx.Files
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files

fun filesTest( build : Files.()->Unit ) {
    val files = Lwjgl3Files()
    with(files) { build() }
}