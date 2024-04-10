package org.yunghegel.salient.modules.io.shared.config

import org.yunghegel.salient.modules.io.*
import org.yunghegel.salient.modules.io.LogLevel.*
import kotlinx.serialization.Serializable

@Serializable
data class LogConfig(
    val log_level: LogLevel = INFO,
    val log_file_name: String = "log.txt",
    val log_file_output_enabled: Boolean = true,
    val log_console_output_enabled: Boolean = true
) {

}
