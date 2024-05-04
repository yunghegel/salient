package src

import com.badlogic.gdx.graphics.g3d.ModelInstance
import org.yunghegel.gdx.meshgen.builder.part.DiscCreator

class DiscTest : ShapeTest() {
    override fun createShape(): ModelInstance  = DiscCreator().instance()
}