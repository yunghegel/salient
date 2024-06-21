package types

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.instances
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.PerformanceCounter
import com.kotcrab.vis.ui.util.IntDigitsOnlyFilter
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel
import com.kotcrab.vis.ui.widget.spinner.Spinner
import com.kotcrab.vis.ui.widget.spinner.SpinnerModel
import ktx.actors.onChange
import ktx.actors.onExit
import ktx.app.clearScreen
import ktx.scene2d.textTooltip

import org.yunghegel.salient.engine.system.perf.profile
import org.yunghegel.gdx.utils.ext.glEnable
import org.yunghegel.gdx.utils.ext.notnull
import org.yunghegel.gdx.utils.ui.Filters
import org.yunghegel.salient.engine.helpers.TextRenderer.drawText
import org.yunghegel.salient.engine.scene3d.TestBundle
import org.yunghegel.salient.engine.system.async
import org.yunghegel.salient.engine.system.async.use
import org.yunghegel.salient.engine.system.perf.ProfileResult
import org.yunghegel.salient.engine.ui.child
import org.yunghegel.salient.engine.ui.label
import org.yunghegel.salient.engine.ui.scene2d.*
import org.yunghegel.salient.engine.ui.table
import org.yunghegel.salient.engine.ui.widgets.value.ReflectionBasedEditor.AccessorRegistry.name
import org.yunghegel.salient.engine.ui.widgets.value.widgets.FloatOnlyFilter


open class Lwjgl3Test (name: String = "Lwjgl3Test") : BaseTest(name) {

    override fun create() {
        bundle.init2D()
        bundle.init3D()
        Gdx.input.inputProcessor = bundle.input
        execCreate()
        if(Gdx.input.inputProcessor is InputMultiplexer) {
            val multiplexer = Gdx.input.inputProcessor as InputMultiplexer
            if (bundle.stage !in multiplexer.processors) {
                multiplexer.addProcessor(bundle.stage)
            }
        }

    }

    var config = Lwjgl3ApplicationConfiguration().apply {
        setTitle(name)
        setWindowedMode(800,600)
    }

    init {

    }

    val bundle : TestBundle by lazy { TestBundle() }

    var drawAxes = true
    var drawRotateHelper = true
    var drawGrid = true
    var padding = 5f
    val createPerf = PerformanceCounter("$name:create")
    val renderPerf = PerformanceCounter("$name:render")

    var createProfile : ProfileResult = ProfileResult()
    var renderProfile : ProfileResult = ProfileResult()

    init {
        config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL32,3,2)
        config.setBackBufferConfig(8, 8, 8, 8, 24, 8, 4)

    }

    override var execCreate : ()->Unit = {}

    override var execRender : ()->Unit = {}

    fun use(block: TestBundle.()->Unit) {
        with(bundle) {
            block()
        }
    }

    fun button(text: String, block: ()->Unit) {
        use {
            val button = STextButton(text) {
                block()
            }
            root.add(button)
        }
    }

    fun option(text: String, value:Boolean,block: (Boolean)->Unit) {
        use {
            val box = SCheckBox(text,value,block )
            root.add(box).pad(padding).row()
        }
    }

    inline fun <reified T> choice(text: String = T::class.simpleName!!, vararg values: T, crossinline map: (T)->String = {it.toString()}, default:T = values.first(), crossinline cb: (T)->Unit) : SSelectBox<String> {
        val box = SSelectBox<String>()
        use {
            val label = label(text)

            root.add(table{
                add(label).padRight(4f)
                add(box)
            }).pad(padding).row()
            }
            val mapped = values.map { map(it) }
            box.setItems(*mapped.toTypedArray())
            box.setSelected(map(default))
            box.onChange {
                val select = box.selected
                val index = mapped.indexOf(select)
                cb(values[index]!!)
            }
        return box
        }



    fun inputFloat(text: String, value: Float, block: (Float)->Unit) {
        use {
            val label = label(text)
            val field = STextField(value.toString())
            field.textFieldFilter = FloatOnlyFilter()
            field.onChange {
                val value = field.text.toFloatOrNull()
                if(value != null) {
                    block(value)
                }
            }
            root.add(table {
                add(label).padRight(4f)
                add(field)
            }).pad(padding)
        }
    }

    fun label(pos: Int, text: ()->String) {

            val label = SLabel(text(),"default-small") {
                text()
            }
        label.setEllipsis(true)
        label.textTooltip(text())

        return bundle.root.add(label).align(pos).pad(padding).row()
    }

    fun inputInt(text: String, value: Int, block: (Int)->Unit) {
        use {
            val label = label(text)
            val field = STextField(value.toString())
            val filter = Filters.intOnly
            field.textFieldFilter = filter
            field.onChange {
                val value = field.text.toIntOrNull()
                if(value != null) {
                    block(value)
                }
            }
            root.add(table {
                add(label).padRight(4f)
                add(field)
            })
        }
    }

    fun inputString(text: String, value: String, block: (String)->Unit) {
        use {
            val label = label(text)
            val field = STextField(value)
            field.onChange {
                block(field.text)
            }
            table {
                add(label).padRight(4f)
                add(field)
            }
        }
    }

    fun spinneri(text: String, value: Int, min: Int, max: Int, block: (Int)->Unit) {
        use {

            val spinner = Spinner(text,IntSpinnerModel(value,min,max))
            spinner.onChange {
                block(spinner.model.text.toInt())
            }
            spinner.onExit {
                stage.scrollFocus = null
            }
            root.add(table {
                add(spinner)
            }).pad(padding)
        }
    }

    fun group(block: Table.()->Unit) {
        val tbl = table()
        use { tbl.block() }
        return bundle.root.add(tbl).row()
    }

    fun row(title:String,block: Table.()->Unit) {
        group {
            add(label(title)).center().growX().row()
            block()
        }
    }

    fun build() {
        glEnable(GL20.GL_DEPTH_TEST)
        glEnable(GL20.GL_CULL_FACE)
        async.init()
        bundle.init()
        profile(name,-1,false) {
            execCreate()
        }
    }

    override fun render() {
            clearScreen(0.1f,0.1f,0.1f,0f)
        if(drawGrid) {
            bundle.grid.render(bundle.cam)
        }
            renderProfile = profile(name,-1,true) {
                execRender()
            }
            drawText("$renderProfile",20f,20f)
            if(bundle.root.children.size > 0) {


                bundle.stage.act()
                bundle.stage.draw()
            }
//        if(drawAxes) {
//            clearDepth()
//            with(bundle) {
//                batch.begin(cam)
//                batch.render(axisArrows)
//                batch.render(rotateHelper)
//                batch.end()
//            }
//
    }

   fun destroy() {
        bundle.dispose()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)

    }

    fun toApplication(cfg: Lwjgl3ApplicationConfiguration) {
        Lwjgl3Application(this,cfg)
    }

    fun run() {
        toApplication(config)
    }

    fun reset() {
        bundle.instances.clear()
        bundle.cam.position.set(2f, 2f, 2f)
        bundle.cam.lookAt(0f, 0f, 0f)
        bundle.init3D()
        bundle.init2D()
        build()
    }

    operator fun invoke() {
        run()
    }



}

fun lwjgl3test(name:String,block : context(Lwjgl3Test,TestBundle)()->Unit) : Lwjgl3Test {
    val app = Lwjgl3Test(name)
    block(app,app.bundle)
    return app
}
