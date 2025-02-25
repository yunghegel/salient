package org.yunghegel.salient.editor.app.configs.camera

import kotlinx.serialization.Serializable
import org.yunghegel.salient.engine.api.Configuration

@Serializable
data class InputConfiguration(val camera: CameraConfiguration = CameraConfiguration()) : Configuration()
