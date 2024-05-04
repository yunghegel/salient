package org.yunghegel.gdx.meshgen.data.attribute.types

import com.badlogic.gdx.graphics.g3d.Material
import org.yunghegel.gdx.meshgen.data.base.Element
import org.yunghegel.gdx.meshgen.data.attribute.ElementAttributeReference

class MaterialElementAttribute<E: Element>(elementAttributeReference: ElementAttributeReference<E>) : ObjectElementAttribute<Material,E>(elementAttributeReference, ArrayFactory {size -> Array(size) { Material() } })
