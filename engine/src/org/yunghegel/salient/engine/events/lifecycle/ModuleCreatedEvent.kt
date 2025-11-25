package org.yunghegel.salient.engine.events.lifecycle

import org.yunghegel.salient.engine.Module
import org.yunghegel.salient.engine.events.BaseEvent
import org.yunghegel.salient.engine.events.Event

@Event("module_created_event", [Module::class])
class ModuleCreatedEvent(val module: Module) : BaseEvent() {



}