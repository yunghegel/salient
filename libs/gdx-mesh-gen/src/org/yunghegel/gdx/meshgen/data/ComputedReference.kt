package org.yunghegel.gdx.meshgen.data


interface ComputedReference<T,D: DirtySyncronized,R: CachedReference<D, *>> {

    fun compute(cachedReference: R): T

}

interface DirtyComputedReference<T,D: DirtySyncronized,R: CachedReference<D, *>> : ComputedReference<T, D, R> {

    val ref : R
    var computed : T

    fun onDirty(cachedReference: R) {
       computed = compute(cachedReference)
    }

    override fun compute(cachedReference: R): T
    {
        if (ref.checkDirty()) {
            onDirty(ref)
        }
        return computed
    }
}

fun <T,D: DirtySyncronized,R: CachedReference<D, *>> R.computed(compute: (R)->T) : DirtyComputedReference<T, D, R> {
    return object : DirtyComputedReference<T, D, R> {
        override val ref: R = this@computed
        override var computed: T = compute(ref)
    }
}

interface CachedReferenceSupplier<D: DirtySyncronized,T: CachedReference<D, *>> {
    fun get() : T
}