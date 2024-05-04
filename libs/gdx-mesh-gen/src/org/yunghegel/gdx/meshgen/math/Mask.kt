package org.yunghegel.gdx.meshgen.math

import kotlin.reflect.KProperty

@JvmInline
value class Mask(val mask:Int) {

    operator fun getValue(thisRef:Any?, property: KProperty<*>) : Int {
        return mask
    }

    infix fun and(other:Mask) : Mask {
        return Mask(mask and other.mask)
    }

    infix fun or(other:Mask) : Mask {
        return Mask(mask or other.mask)
    }

    infix fun xor(other:Mask) : Mask {
        return Mask(mask xor other.mask)
    }

    infix fun set(other:Mask) : Mask {
        return Mask(mask or other.mask)
    }

    infix fun clear(other:Mask) : Mask {
        return Mask(mask and other.mask.inv())
    }

    infix fun flip(other:Mask) : Mask {
        return Mask(mask xor other.mask)
    }

    infix fun andNot(other:Mask) : Mask {
        return Mask(mask and other.mask.inv())
    }

    infix fun has(other:Mask) : Boolean {
        return mask and other.mask == other.mask
    }

    operator fun not() : Mask {
        return Mask(mask.inv())
    }

    fun set(bit:Int, value:Boolean) : Mask {
        return if (value) {
            Mask(mask or (1 shl bit))
        } else {
            Mask(mask and (1 shl bit).inv())
        }
    }

    fun get(bit:Int) : Boolean {
        return mask and (1 shl bit) != 0
    }

}