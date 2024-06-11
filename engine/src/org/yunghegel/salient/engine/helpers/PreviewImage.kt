package org.yunghegel.salient.engine.helpers

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer
import com.badlogic.gdx.math.MathUtils.degreesToRadians
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.ScreenUtils
import ktx.assets.toAbsoluteFile
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute
import net.mgsx.gltf.scene3d.utils.MaterialConverter
import org.yunghegel.gdx.utils.ext.MathUtils
import org.yunghegel.gdx.utils.ext.convertToPBR
import org.yunghegel.gdx.utils.ext.instance
import org.yunghegel.salient.engine.scene3d.SceneContext
import org.yunghegel.salient.engine.scene3d.SceneGraphicsResources
import javax.swing.text.html.CSS.Attribute.BACKGROUND_COLOR
import kotlin.math.tan

class PreviewImage(val model : Model, val context : SceneContext) : SceneGraphicsResources by context {

    val texture : Texture = generate()
    val tmp = mutableListOf<Material>()

    init {
        model.materials.forEach { mat ->

            MaterialConverter.makeCompatible(mat)

        }

    }

    fun save(path: String,pixmap :Pixmap) {
        val file = path.toAbsoluteFile()
        PixmapIO.writePNG(file, pixmap)
    }

    fun generate(path:String?=null,width: Int = 250, height: Int = 250, bgcolor: Color = Color(0.1f, 0.1f, 0.1f,0f)) : Texture {
            val cam = PerspectiveCamera(75f,width.toFloat(),height.toFloat())
            val bounds = BoundingBox()
            model.calculateBoundingBox(bounds)

            val center = Vector3()
            val dimensions = Vector3()
            bounds.getCenter(center)
            bounds.getDimensions(dimensions)

            val maxDimension = Math.max(dimensions.x, Math.max(dimensions.y, dimensions.z))
            val distance =
                (maxDimension / (2 * tan((cam.fieldOfView / 2 * degreesToRadians))))

            var camFar = center.dst(bounds.getCorner000(Vector3())) + distance
            camFar += 100f // Add additional buffer to distance

            // Position up a bit, looking down at model
            val verticalDistance = distance * 0.25f

            cam.position.set(center.x + distance, center.y + verticalDistance, center.z + distance)
            cam.lookAt(center)
            cam.near = 0.1f
            cam.far = camFar
            cam.update()

            val frameBufferBuilder = GLFrameBuffer.FrameBufferBuilder(width, height)
            frameBufferBuilder.addBasicColorTextureAttachment(Pixmap.Format.RGBA8888)

            // Enhanced precision, only needed for 3D scenes
            frameBufferBuilder.addDepthRenderBuffer(GL30.GL_DEPTH_COMPONENT24)
            val fbo = frameBufferBuilder.build()

            fbo.begin()
            ScreenUtils.clear(bgcolor, true)
            batch.begin(cam)
            batch.render(model.instance, context)
            batch.end()

            val p = Pixmap.createFromFrameBuffer(0, 0, width, height)
            // Flip the pixmap
            val flipped = Pixmap(p.width, p.height, p.format)
            for (x in 0 until p.width) {
                for (y in 0 until p.height) {
                    flipped.drawPixel(x, p.height - 1 - y, p.getPixel(x, y))
                }
            }

            fbo.end()

            val texture = Texture(flipped)

            if(path!=null) {
                save(path,flipped)
            }

            p.dispose()
            fbo.dispose()
            flipped.dispose()

            return texture
    }

    companion object {
        val batch = ModelBatch()
    }

}