
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import org.lwjgl.opengl.GL11C
import org.yunghegel.gdx.utils.ext.appheightf
import org.yunghegel.gdx.utils.ext.appwidthf
import org.yunghegel.gdx.utils.ext.boundingRadius
import org.yunghegel.gdx.utils.ext.notnull
import org.yunghegel.salient.engine.helpers.SampleModels
import kotlin.math.acos
import kotlin.math.min
import kotlin.math.sqrt

val ArcBallTest  =  lwjgl3test {

    var arcballController: ArcballTool by notnull()

    execCreate = {
        bundle.init3D(modelenum = SampleModels.SUZANNE)
        bundle.apply {
            arcballController = ArcballTool(instance.transform,model.boundingRadius)
            Gdx.input.inputProcessor = arcballController
        }
    }

    execRender = {
        bundle.draw3D()
        bundle.apply {
            arcballController.render(cam,shapeRenderer,Gdx.input.x.toFloat(),Gdx.input.y.toFloat())
        }
    }

}

fun main() = ArcBallTest()



class ArcballTool(val target: Matrix4,val radius:Float) : InputAdapter() {
    private var isDragging = false
    private var lastX = 0
    private var lastY = 0
    private var lastMousePosition = Vector3()
    private val tmp = Vector3()
    private val start = Vector3()
    private val end = Vector3()

    private val targetRotation = Quaternion()
    private val currentRotation = Quaternion()
    private val interpolatedRotation = Quaternion()
    private val lerpFactor = 0.1f // Adjust this value to control the smoothness of rotation

    private val lineStart = Vector3()
    private val lineEnd = Vector3()

    private fun getArcballVector(x: Int, y: Int): Vector3 {
        val screenRatioX = appwidthf
        val screenRatioY = appheightf

        val p = Vector3((2 * x / screenRatioX) - 1, (2 * y / screenRatioY) - 1, 0f)
        p.y *= -1f

        val OPsquared = p.x * p.x + p.y * p.y

        if (OPsquared <= 1 * 1)  { // ?
            p.z = sqrt((1 * 1 - OPsquared)) // Pythagoras
        } else {
            p.nor()

        }

        if (p.x * p.x + p.y * p.y <= 1f) {
            lineEnd.set(p.x, p.y, sqrt(1 - p.x * p.x - p.y * p.y))
        } else {
            lineEnd.set(p.x / sqrt(p.x * p.x + p.y * p.y), p.y / sqrt(p.x * p.x + p.y * p.y), 0f)
        }


        return p
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        start.set(getArcballVector(screenX, screenY))
        return super.mouseMoved(screenX, screenY)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        currentRotation.set(target.getRotation(Quaternion()))
        isDragging = true
        lastMousePosition.set(screenX.toFloat(), screenY.toFloat(), 0f)
        lastX = screenX
        lastY = screenY

        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        isDragging = false
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (isDragging) {
            val va = getArcballVector(lastX, lastY)
            val vb = getArcballVector(screenX, screenY)

            val angle = acos(min(1f, va.dot(vb)))
            val axis = va.crs(vb).nor()

            target.rotate(axis, angle * 180 / Math.PI.toFloat())

            lastX = screenX
            lastY = screenY

        }
        return true
    }

    fun render(camera: Camera,shapeRenderer: ShapeRenderer,x:Float,y:Float) {
        val ray = camera.getPickRay(x, y)

        shapeRenderer.apply {
            projectionMatrix = camera.combined
            begin(ShapeRenderer.ShapeType.Line)
            color = Color.WHITE
            line(Vector3.Zero,lineEnd)
            GL11C.glPointSize(4f)
            color = Color.CORAL
            set(ShapeRenderer.ShapeType.Point)
            point(lineEnd.x,lineEnd.y,lineEnd.z)
            end()

        }
    }
}