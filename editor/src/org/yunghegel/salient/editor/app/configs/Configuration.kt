package org.yunghegel.salient.editor.app.configs

@Serializable
abstract class Configuration {

    @Transient
    var sync_actions = mutableListOf<SyncAction<*>>()

    fun registerSyncAction(action: SyncAction<*>) {
        sync_actions.add(action)
    }

    fun sync() {
        sync_actions.forEach { it.invoke() }
    }

    inner class ConfigItemDelegate<R, T>(var value: T) : Delegate<R, T> {

        override fun getValue(r: R, p: KProperty<*>): T {
            return value
        }

        override fun setValue(r: R, p: KProperty<*>, value: T) {
            this.value = value
        }
    }

}