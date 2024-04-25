package org.yunghegel.gdx.utils.data

class ObservableMap<T,V> : HashMap<T,V>() {

    val listeners = ArrayList<MapHandler<T,V>>()

    fun listen(conf: MapAdapter.Builder<T,V>.()->Unit) {
        listeners.add(MapAdapter.Builder<T,V>(conf).build())
    }

    override fun put(key: T, value: V): V? {
        val result = super.put(key,value)
        listeners.forEach { it.onAdd(key,value) }
        return result
    }

    override fun remove(key: T): V? {
        val result = super.remove(key)
        listeners.forEach { it.onRemove(key,result!!) }
        return result
    }

    override fun putAll(from: Map<out T, V>) {
        super.putAll(from)
        from.forEach { (key,value) -> listeners.forEach { it.onAdd(key,value) } }
    }

    override fun clear() {
        super.clear()
    }

}

interface MapHandler<T,V> {

    fun onAdd(key: T,value: V)

    fun onRemove(key: T,value: V)

    fun onUpdate(key: T,value: V)


}

abstract class MapAdapter<T,V>  :MapHandler<T,V> {

    class Builder<T,V>(conf: MapAdapter.Builder<T,V>.()->Unit) {

        var onAdd: (T,V)->Unit = { _,_ -> }

        var onRemove: (T,V)->Unit = { _,_ -> }

        var onUpdate: (T,V)->Unit = { _,_ -> }

        fun build() : MapAdapter<T,V> {
            return object : MapAdapter<T,V>() {
                override fun onAdd(key: T, value: V) {
                    this@Builder.onAdd(key,value)
                }

                override fun onRemove(key: T, value: V) {
                    this@Builder.onRemove(key,value)
                }

                override fun onUpdate(key: T, value: V) {
                    this@Builder.onUpdate(key,value)
                }
            }

        }

    }

}