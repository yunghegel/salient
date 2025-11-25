package classpath

import org.yunghegel.salient.engine.helpers.reflect.ClasspathScanner
import org.yunghegel.salient.engine.helpers.reflect.iterateProperties
import kotlin.test.Test

class ScannerTest {

    @Test
    fun `test classpath scanner`() {
        val scannedClasses = ClasspathScanner.scan("org.yunghegel.salient.engine")
        println("Scanned ${scannedClasses.size} classes:")
        scannedClasses.forEach { info ->
            println("Class: ${info.name}")
            info.iterateProperties { fields, methods, interfaces ->
                StringBuilder().apply {
                    appendLine("  Fields:")
                    fields.forEach { field ->
                        appendLine("    - ${field.name}: ${field.type}")
                    }
                    appendLine("  Methods:")
                    methods.forEach { method ->
                        appendLine("    - ${method.name}(): ${method.returnType}")
                    }
                    appendLine("  Interfaces:")
                    interfaces.forEach { interfaceName ->
                        appendLine("    - $interfaceName")
                    }
                    println(this.toString())
                }
            }
        }
    }

}