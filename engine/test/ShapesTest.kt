import com.badlogic.gdx.Gdx
import org.yunghegel.gdx.lwjgl.par.ParPrimitives
import org.yunghegel.gdx.utils.ext.floatBufferToArrayCopy
import java.util.*
import kotlin.test.Test

class ShapesTest {

    @Test fun  test_par(){
        lwjgl3test {
            execCreate = {
                Gdx.app.log("","Hello, world!")
                val par = ParPrimitives()
                val cylinder = par.createCylinder(10, 10)

            }
        }
    }
}