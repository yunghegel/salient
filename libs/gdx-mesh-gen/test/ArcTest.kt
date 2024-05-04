package src

import org.yunghegel.gdx.meshgen.builder.part.ArcCreator

class ArcTest : ShapeTest() {

    override fun createShape() = ArcCreator().instance()

}