package org.yunghegel.salient.engine.ui.widgets.viewport

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ArrowShapeBuilder
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder
import com.badlogic.gdx.math.*
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.math.collision.Ray
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener

import ktx.collections.GdxArray
import org.yunghegel.gdx.utils.every


class Compass(val camera: PerspectiveCamera) :InputListener()  {

    private val ARROW_LENGTH = 0.05f
    private val ARROW_THIKNESS = .3f
    private val ARROW_CAP_SIZE = .1f
    private val SPHERE_SIZE = 0.008f
    private val ARROW_DIVISIONS = 15

    private val COLOR_X = Color(.8f, 0f, 0f, 1f)
    private val COLOR_Y = Color(0f, .8f, 0f, 1f)
    private val COLOR_Z = Color.valueOf("#0074F7")

    private val ID_X = 0
    private val ID_Y = 1
    private val ID_Z = 2

    private val ID_NEG_X = 3
    private val ID_NEG_Y = 4
    private val ID_NEG_Z = 5

    var position = Vector3(.87f, .8f, 0f)

    var tempv3 = Vector3()
    var localCam = PerspectiveCamera()

    val bounds: BoundingBox
    val handles = GdxArray<CompassHandle>()
    var hoveredHandle: CompassHandle? = null
    var previousHoveredHandle: CompassHandle? = null
    var intersectedIdx = -1

    var rect: Rectangle? = null

    val attr: ColorAttribute?
        get() {
            if (intersectedIdx == ID_X || intersectedIdx == ID_NEG_X) {
                return ColorAttribute.createDiffuse(Color.RED)
            }
            if (intersectedIdx == ID_Y || intersectedIdx == ID_NEG_Y) {
                return ColorAttribute.createDiffuse(Color.GREEN)
            }
            if (intersectedIdx == ID_Z || intersectedIdx == ID_NEG_Z) {
                return ColorAttribute.createDiffuse(Color.BLUE)
            }
            return null
        }

    val modelBatch: ModelBatch

    init {
        modelBatch = ModelBatch()
        handles.add(CompassHandle(COLOR_X, Vector3(ARROW_LENGTH, 0f, 0f), ID_X))
        handles.add(CompassHandle(COLOR_Y, Vector3(0f, ARROW_LENGTH, 0f), ID_Y))
        handles.add(CompassHandle(COLOR_Z, Vector3(0f, 0f, ARROW_LENGTH), ID_Z))
        handles.add(CompassHandle(COLOR_X, Vector3(-ARROW_LENGTH+.003f, 0f, 0f), ID_NEG_X))
        handles.add(CompassHandle(COLOR_Y, Vector3(0f, -ARROW_LENGTH+0.003f, 0f), ID_NEG_Y))
        handles.add(CompassHandle(COLOR_Z, Vector3(0f, 0f, -ARROW_LENGTH+0.003f), ID_NEG_Z))
        bounds = BoundingBox()
        bounds.ext(Vector3(ARROW_LENGTH, 0f, 0f))
        bounds.ext(Vector3(0f, ARROW_LENGTH, 0f))
        bounds.ext(Vector3(0f, 0f, ARROW_LENGTH))
        bounds.ext(Vector3(-ARROW_LENGTH, 0f, 0f))
        bounds.ext(Vector3(0f, -ARROW_LENGTH, 0f))
        bounds.ext(Vector3(0f, 0f, -ARROW_LENGTH))

        localCam.near = 0.01f
        localCam.far = 1000f


    }

    fun setPos(x:Float,y:Float){
        position.x = x
        position.y = y
    }

    private fun setCompassPosition() {
        position.z = -0.5f
        handles.forEach { handle ->
            handle.modelInstance.transform.setTranslation(position)
        }

        var screenSpace = Vector3()
        val rotMat = Matrix4().setToRotation(camera.direction, Vector3.Y)
        val invRotMat = rotMat.inv()
        handles.forEach {
            val dir = it.direction.cpy().mul(invRotMat)
            screenSpace = camera.project(dir)
        }
        val center = camera.project(position)
        val screenSpaceCenter = Vector2(center.x, center.y)
    }

    fun render(delta: Float) {

            modelBatch.begin(localCam)
            handles.forEach { handle ->
                modelBatch.render(handle.modelInstance)
            }
            modelBatch.end()
    }



    fun checkIntersection(): Int {

        if (rect == null) return 0
        val ray =
            localCam.getPickRay(
                Gdx.input.x.toFloat(),
                Gdx.input.y.toFloat(),
                rect!!.x,
                rect!!.y,
                rect!!.width,
                rect!!.height
            )
        handles.forEach { handle ->
            if (handle.intersectsRay(ray)) {
                return handle.id
            }
        }
        return -1
    }

    fun update(delta: Float,width:Float,height:Float) {
        localCam.viewportWidth = width
        localCam.viewportHeight = height
        setCompassPosition()
        handles.forEach { handle ->
            handle.modelInstance.transform.getTranslation(tempv3)
            handle.modelInstance.transform.set(camera.view)
            handle.modelInstance.transform.setTranslation(tempv3)
        }
        val intersection = checkIntersection()
        if (intersection != -1) {
            intersectedIdx = intersection
            hoveredHandle = handles.firstOrNull { handle -> handle.id == intersection }
            if (hoveredHandle != null) {
                hoveredHandle!!.modelInstance.materials.first().set(attr)
            }
        }
    }

    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        intersectedIdx = checkIntersection()
        if (intersectedIdx != -1) {
            hoveredHandle = handles.firstOrNull { handle -> handle.id == intersectedIdx }
            if (hoveredHandle != null) {
                orientCamera(intersectedIdx)
            }
        }
        return super.touchDown(event, x, y, pointer, button)
    }

    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
        if (hoveredHandle != null) {
            hoveredHandle!!.modelInstance.materials.first().set(hoveredHandle!!.attr)
            hoveredHandle!!.clicked = false
            hoveredHandle!!.hovered = false
            hoveredHandle!!.wasHovered = false
            hoveredHandle = null
        }
        super.touchUp(event, x, y, pointer, button)
    }

    override fun mouseMoved(event: InputEvent?, x: Float, y: Float): Boolean {
        val intersection = checkIntersection()
        if (intersection != -1) {
            println("intersection: $intersection")
            intersectedIdx = intersection
            previousHoveredHandle = hoveredHandle
            hoveredHandle = handles.firstOrNull { handle -> handle.id == intersection }
            if (hoveredHandle != null) {
                hoveredHandle!!.wasHovered = true
                handles.forEach { handle ->
                    if (handle != hoveredHandle && handle.wasHovered) {
                        handle.modelInstance.materials.first().set(handle.attr)
                        handle.hovered = false
                        handle.wasHovered = false
                    }
                }

                hoveredHandle!!.modelInstance.materials.first().set(attr)
            }
        } else if (hoveredHandle != null) {
            hoveredHandle!!.modelInstance.materials.first().set(hoveredHandle!!.attr)
            hoveredHandle!!.hovered = false
            hoveredHandle!!.wasHovered = false
            hoveredHandle = null
        }
        return super.mouseMoved(event, x, y)
    }

    fun orientCamera(id: Int) {
//        if (id == ID_X) {
//            UI.cameraController.lookAtAxis(Axis.X)
//        }
//        if (id == ID_Y) {
//            UI.cameraController.lookAtAxis(Axis.Y)
//        }
//        if (id == ID_Z) {
//            UI.cameraController.lookAtAxis(Axis.Z)
//        }
//        if (id == ID_NEG_X) {
//            UI.cameraController.lookAtAxis(Axis.NEG_X)
//        }
//        if (id == ID_NEG_Y) {
//            UI.cameraController.lookAtAxis(Axis.NEG_Y)
//        }
//        if (id == ID_NEG_Z) {
//            UI.cameraController.lookAtAxis(Axis.NEG_Z)
//        }
    }

    inner class CompassHandle(val color: Color, val direction: Vector3, val id: Int)  {

        val model: Model
        val modelInstance: ModelInstance
        val mat: Material

        var hovered = false
        var wasHovered = false
        var clicked = false

        val bounds: BoundingBox
            get() {
                modelInstance.calculateBoundingBox(boundingBox)
                boundingBox.mul(modelInstance.transform)
                return boundingBox
            }

        val boundingBox: BoundingBox = BoundingBox()
        val attr: ColorAttribute = ColorAttribute.createDiffuse(color)

        init {

            mat = Material()
            mat.set(attr)

            val modelBuilder = ModelBuilder()
            modelBuilder.begin()
            val builder = modelBuilder.part(
                "compass",
                GL20.GL_TRIANGLES,
                (VertexAttributes.Usage.Position).toLong(),
                mat
            )
            if (id == ID_X || id == ID_Y || id == ID_Z) {
                ArrowShapeBuilder.build(
                    builder,
                    0f,
                    0f,
                    0f,
                    direction.x,
                    direction.y,
                    direction.z,
                    ARROW_CAP_SIZE,
                    ARROW_THIKNESS,
                    ARROW_DIVISIONS
                )
            } else {
                builder.setVertexTransform(Matrix4().translate(direction))
                SphereShapeBuilder.build(
                    builder,
                    SPHERE_SIZE,
                    SPHERE_SIZE,
                    SPHERE_SIZE,
                    15,
                    15
                )

            }

            model = modelBuilder.end()
            modelInstance = ModelInstance(model)
        }

        fun intersectsRay(ray: Ray): Boolean {
            return Intersector.intersectRayBoundsFast(ray, bounds)
        }

    }



}
