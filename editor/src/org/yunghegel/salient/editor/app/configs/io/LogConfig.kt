package org.yunghegel.salient.editor.app.configs.io

import kotlinx.serialization.Serializable
import org.yunghegel.salient.engine.system.LogLevel

@Serializable
data class LogConfig(
    val log_level: LogLevel = LogLevel.Info,
    val log_file_name: String = "log.txt",
    val log_file_output_enabled: Boolean = true,
    val log_console_output_enabled: Boolean = true
)
