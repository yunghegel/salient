package org.yunghegel.gdx.utils.data

interface Mask {

    open val set : Bitset
        get() = Bitset()

    var mask : Int

    open val default : Int
        get() = 0

    fun set(value: Int): Int {
        mask =  value or mask
        return mask
    }

    fun set(name:String): Int {
        val flag = set.registry[name] ?: return 0
        return set(flag)
    }

    fun set(vararg int: Int): Int {
        for (value in int) {
            mask = mask or value
        }
        return mask
    }

    fun set(vararg names: String): Int {
        for (name in names) {
            val flag = set.registry[name] ?: continue
            mask = mask or flag
        }
        return mask
    }


    fun clear(name:String): Int {
        val flag = set.registry[name] ?: return 0
        return clear(flag)
    }

    fun clear(value: Int): Int {
        mask = value and mask.inv()
        return mask
    }

    fun toggle(value: Int): Int {
        mask = value xor mask
        return mask
    }

    fun toggle(name:String): Int {
        val flag = set.registry[name] ?: return 0
        return toggle(flag)
    }

    fun has(value: Int): Boolean {
        return value and mask != 0
    }

    fun has(name:String): Boolean {
        val flag = set.registry[name] ?: return false
        return has(flag)
    }

    fun allOf(vararg int: Int): Boolean {
        for (value in int) {
            if (value and mask == 0) {
                return false
            }
        }
        return true
    }

    fun noneOf(value: Int): Boolean {
        return value and mask == 0
    }

    fun forEachTrue(action: (Int) -> Unit) {
        var mask = mask
        while (mask != 0) {
            val flag = Integer.lowestOneBit(mask)
            action(flag)
            mask = mask xor flag
        }
    }

    fun forEach(action: (String,Boolean)->Unit) = set.registry.forEach { (name, flag) -> action(name, has(flag)) }

    fun maskOf(vararg values: Int): Int {
        var mask = 0
        for (value in values) {
            mask = mask or value
        }
        return mask
    }

    fun string(): String {
        val builder = StringBuilder()
        builder.append("Mask: ")
        forEachTrue { flag ->
            builder.append(set.registry.entries.find { it.value == flag }?.key)
            builder.append(", ")
        }
        return builder.toString()
    }

}