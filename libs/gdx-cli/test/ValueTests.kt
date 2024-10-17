
import org.yunghegel.gdx.cli.CommandLine
import org.yunghegel.gdx.cli.arg.Command
import org.yunghegel.gdx.cli.arg.Namespace
import org.yunghegel.gdx.cli.arg.Value
import org.yunghegel.gdx.cli.util.Type
import org.yunghegel.gdx.cli.value.CLIValue
import org.yunghegel.gdx.utils.reflection.KAccessor
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.test.Test

class ValueTests {

    val cli = CommandLine()
    val values = Values()

    fun scan(obj: Any, namespace : String = "global") : List<CLIValue>{
        val values : MutableList<CLIValue> = mutableListOf()
        val props = obj::class.members
        for (prop in props) {
            if (prop is KMutableProperty1<*,*>) {
                val valueAnnotation = prop.annotations.find { it is Value } as Value?
                valueAnnotation?.let { annotation ->
                    val value = prop.getter.call(obj)
                    val accessor : KAccessor = object : KAccessor {
                        override fun get(): Any {
                            return prop.getter.call(obj) as Any
                        }

                        override fun set(value: Any) {
                            if (annotation.type.matches(value)) {
                                prop.setter.call(obj, value)
                            } else {
                                println("Value $value does not match type ${annotation.type}")
                            }
                        }

                        override val name: String
                            get() = prop.name

                        override val type: KClass<*>
                            get() = prop.returnType.classifier as KClass<*>
                    }
                    val cliVal = CLIValue(accessor, annotation)
                    values.add(cliVal)
                }

            }
        }
        return values
    }

    @Test
    fun testValues() {
        cli.register(values)
        val input = arrayOf("set primitives.string hello","get primitives.string","set primitives.int 5","get primitives.int","set primitives.float 3.14","get primitives.float","set primitives.bool true","get primitives.bool","help primitives.bool")
        input.forEach { cli.acceptInput(it) }
    }



}

@Namespace("primitives")
class Values {

    @Value(Type.STRING, "A string value")
    var string : String = "StringValue"

    @Value(Type.INT, "An integer value")
    var int : Int = 0

    @Value(Type.FLOAT, "A float value")
    var float : Float = 0f

    @Value(Type.BOOLEAN, "A boolean value")
    var bool : Boolean = false

    @Command("sayHello")
    fun sayHello() {
        println("Hello!")
    }

}