package org.yunghegel.salient.modules.input.shared.config

import org.yunghegel.salient.common.shared.config.*
import kotlinx.serialization.Serializable

@Serializable
data class InputConfiguration(val camera: CameraConfiguration = CameraConfiguration()) : Configuration()
