package org.yunghegel.salient.modules.io.shared.config

import org.yunghegel.salient.common.shared.config.*
import kotlinx.serialization.Serializable

@Serializable
data class IOConfiguration(val log: LogConfig = LogConfig(), val auto_save: Boolean = true) : Configuration() {

}
