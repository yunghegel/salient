import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.utils.Align
import org.yunghegel.gdx.utils.ext.clearColor
import org.yunghegel.gdx.utils.ext.clearDepth
import org.yunghegel.gdx.utils.ext.each
import org.yunghegel.salient.engine.ui.Icons
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.SImage
import org.yunghegel.salient.engine.ui.scene2d.STable

class DnDTest : ApplicationAdapter(){

    lateinit var stage : Stage
    lateinit var root : STable

    lateinit var groupOne : STable
    lateinit var groupTwo : STable

    lateinit var dnd : DragAndDrop

    override fun create() {
        UI.init()
        stage = Stage()
        root = STable()
        root.setFillParent(true)
        root.align(Align.center)
        stage.addActor(root)

        groupOne = STable()
        groupTwo = STable()
        groupTwo.touchable = Touchable.enabled
        groupOne.touchable = Touchable.enabled
        groupOne.debugTable()
        groupTwo.debugTable()

        groupOne.add(Icons.CONE.img).pad(20f)
        groupOne.add(Icons.CUBE.img).pad(20f)
        groupOne.add(Icons.CYLINDER.img).pad(20f)
        groupOne.pad(10f)

        groupTwo.add(Icons.SPHERE.img).pad(20f)
        groupTwo.add(Icons.PLANE.img).pad(20f)
        groupTwo.add(Icons.TORUS.img).pad(20f)
        groupTwo.pad(10f)

        root.add(groupOne).pad(30f)

        root.row()

        root.add(groupTwo).pad(30f)

        createDND()

        Gdx.input.inputProcessor = stage

    }

    fun overItem(x: Float, y: Float): SImage? {
        val items = listOf(groupOne, groupTwo).flatMap { it.children.filterIsInstance<SImage>() }
        return items.find { it.hit(x, y, true) != null }
    }

    fun overGroup(x: Float, y: Float): STable? {
        val groups = listOf(groupOne, groupTwo)
        return groups.find { it.hit(x, y, true) != null }
    }

    fun createDND() {
        dnd = DragAndDrop()

        val items = listOf(groupOne, groupTwo).flatMap { it.children.filterIsInstance<SImage>() }
        items.each { it.touchable = Touchable.enabled}
        val sources = listOf(groupOne,groupTwo)
        sources.each { item ->
            dnd.addSource(object : DragAndDrop.Source(item) {
            override fun dragStart(event: InputEvent?, x: Float, y: Float, pointer: Int): DragAndDrop.Payload {
                val payload = DragAndDrop.Payload()
                (item.hit(x,y,true)?.let { it
//                    look for the child actor we're over
                    it.debug()
                    if (it is SImage) {
                        payload.dragActor = SImage(it.drawable)
                        payload.`object` = it
                    } else if (it is STable) {
                        it.children.find { it.hit(x,y,true)!=null }?.let {
                            payload.`object` = it
                            payload.dragActor = it
                        }
                    }

                })
                return payload
            }
        } )}

        val targets = listOf(groupOne, groupTwo).each {group -> dnd.addTarget(object : DragAndDrop.Target(group) {
            override fun drag(source: DragAndDrop.Source?, payload: DragAndDrop.Payload?, x: Float, y: Float, pointer: Int): Boolean {
                (group.hit(x,y,true)?.let { target ->
                    if (target is STable) {
                        if (target.children.contains(payload?.`object`)) {

                        } else {

                            return true
                        }
                    }
                })
               return false
            }

            override fun reset(source: DragAndDrop.Source?, payload: DragAndDrop.Payload?) {

                super.reset(source, payload)
            }

            override fun drop(
                source: DragAndDrop.Source?,
                payload: DragAndDrop.Payload?,
                x: Float,
                y: Float,
                pointer: Int
            ) {
                if (payload?.`object`==null) return
                val src = payload.`object` as Actor
                (group.hit(x,y,true)?.let { target ->
                    if (target is STable) {
                        if (target.children.contains(src)) {

                        } else {
                            src.remove()
                            target.add(src)

                        }
                    } else if (target is SImage) {
                        val parent = target.parent as STable
                        if (parent.children.contains(src)) {

                        } else {
                            src.remove()
                            parent.add(src)
                        }
                    }
                })
            }
        })
    }
    }

    override fun render() {
        clearColor()
        clearDepth()
        stage.act()
        stage.draw()
    }



}

fun main() {
    val dndTest = DnDTest()
    Lwjgl3Application(dndTest, Lwjgl3ApplicationConfiguration())
}