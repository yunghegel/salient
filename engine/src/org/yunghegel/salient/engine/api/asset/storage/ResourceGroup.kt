package org.yunghegel.salient.engine.api.asset.storage

import org.yunghegel.gdx.utils.data.ObservableMap
import org.yunghegel.gdx.utils.ext.each
import org.yunghegel.salient.engine.api.Checksum
import org.yunghegel.salient.engine.api.Named
import org.yunghegel.salient.engine.api.NamedObjectResource
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.io.Filepath
import java.util.HashMap
import java.util.UUID

class ResourceGroup(loc : String,_name:String) : NamedObjectResource {

    val checksum = Checksum()

    override val path: Filepath = Filepath(loc)

    override val name: String = _name

    override val uuid: String = UUID.fromString(path.path).toString()

    override val id: Long
        get() = checksum.sum.value



    val _index : ObservableMap<String,String> = ObservableMap()

    fun joinMap(map: Map<String,String>) : String {
    var sb = StringBuilder()
        map.entries.forEachIndexed { index,entry ->
            sb.append("$[{entry.key}]:${entry.value}")
        }
        return sb.toString()
    }

    init {
        _index.listen {
            onRemove = { k,v ->
                println("Removed $k with value $v")
                checksum.from(joinMap(_index))
            }
            onAdd = {k,v ->
                println("Added $k with value $v")
                checksum.from(joinMap(_index))
            }
            onUpdate = { k,v ->
                println("Updated $k to value $v")
                checksum.from(joinMap(_index))
            }
        }
    }

    fun index(asset: AssetHandle) {
        _index[asset.name] = asset.uuid
    }


}