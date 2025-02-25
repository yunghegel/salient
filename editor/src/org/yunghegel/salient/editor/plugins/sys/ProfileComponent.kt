package org.yunghegel.salient.editor.plugins.sys

import com.badlogic.ashley.core.ComponentMapper
import org.yunghegel.gdx.utils.data.CircularList
import org.yunghegel.salient.engine.api.ecs.BaseComponent
import org.yunghegel.salient.engine.system.Perf
import kotlin.reflect.KClass

class ProfilerComponent : BaseComponent() {
    override val type: KClass<out BaseComponent> = ProfilerComponent::class

    var metrics: CircularList<Perf.Metric> = CircularList(100)

    var id: Int = 0

    fun start(name: String) {
        id = Perf.start("$name$id")
    }

    fun end(name: String) {
        val metric = Perf.end(id)
        metrics.add(metric)
    }


    companion object {
        val mapper: ComponentMapper<ProfilerComponent> = ComponentMapper.getFor(ProfilerComponent::class.java)
    }


}