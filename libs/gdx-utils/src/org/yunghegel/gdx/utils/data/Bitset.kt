package org.yunghegel.gdx.utils.data

open class Bitset() {

    val registry : Map<String,Int> = mutableMapOf()

    private var last = 0

    private val flag : Int
        get() = 1 shl last++

    fun register(name : String){
        val msk = flag
        (registry as MutableMap)[name] = msk
        println("Registered $name with mask $msk")
    }

    fun unregister(name : String){
        (registry as MutableMap).remove(name)
    }










}