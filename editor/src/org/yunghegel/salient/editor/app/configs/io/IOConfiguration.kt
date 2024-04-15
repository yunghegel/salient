package org.yunghegel.salient.modules.io.shared.config

import kotlinx.serialization.Serializable
import org.yunghegel.salient.editor.app.configs.Configuration
import org.yunghegel.salient.editor.app.configs.io.LogConfig

@Serializable
data class IOConfiguration(val log: LogConfig = LogConfig(), val auto_save: Boolean = true) : Configuration()
