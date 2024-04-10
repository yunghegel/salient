import ktx.reflect.Reflection
import ktx.reflect.reflect
import org.junit.Test
import org.yunghegel.salient.engine.helpers.reflect.annotation.Persistent
import org.yunghegel.salient.engine.helpers.reflect.ClasspathScanner

class ReflectionTests {

    @Test
    fun test_classpath_scanner_scan() {
        val scanner = ClasspathScanner("org.yunghegel.salient.engine.ui.scene2d")
        assert(scanner.classes.isNotEmpty())
    }

    @OptIn(Reflection::class)
    @Test
    fun test_classpath_scanner_annotations() {
        val scanner = ClasspathScanner("org.yunghegel.salient.engine.ui")
        scanner.run {
            process { info ->
                reflect(info.load().kotlin).declaredFields.forEach {
                    if (it.isAnnotationPresent(Persistent::class.java)) {
                        println("Field ${it.name} is annotated with @Persistent")
                    }
                }

            }

        }
    }
}