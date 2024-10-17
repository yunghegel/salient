import com.badlogic.gdx.math.Matrix4
import org.yunghegel.gdx.utils.ext.Printer
import kotlin.test.Test

class PrinterTest {

    @Test fun `test_printer_functionality`() {
        val printer = Printer(Matrix4::class.java, Printer.Label("test",Matrix4()),)
        println(printer.toString())
    }

}