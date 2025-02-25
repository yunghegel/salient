package org.yunghegel.salient.editor.plugins.sys.systems

import org.yunghegel.salient.editor.plugins.BaseSystem
import org.yunghegel.salient.engine.system.Perf

class ProfilerSystem : BaseSystem("profiler_system") {

    val metrics: MutableMap<String, Perf.Metric> = mutableMapOf()

    init {
        Perf.listen { metric, name ->
            metrics[name] = metric
        }

    }


}