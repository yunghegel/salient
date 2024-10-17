
import com.badlogic.gdx.graphics.Texture
import types.headlessTest
import kotlin.test.Test

class FilesTests {

    @Test fun test_classpath() = headlessTest {
        val file = Texture("icon.png")
    }

}