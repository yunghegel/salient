import org.yunghegel.gdx.lwjgl.par.ParPrimitives
import org.yunghegel.gdx.lwjgl.par.headlessTest
import kotlin.test.Test

class ParTests {

    @Test fun par_create_cylinder() {
        headlessTest {
        val par = ParPrimitives()
        val cylinder = par.createCylinder(10, 10)
        val size = cylinder?.npoints()
        val positions = cylinder?.normals(size!! * 3)
        val arr = positions!!.array()
            println(size)
        println(arr)

    }
    }

}