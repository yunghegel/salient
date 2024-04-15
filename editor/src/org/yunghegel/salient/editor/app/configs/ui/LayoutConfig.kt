package org.yunghegel.salient.editor.app.configs.ui

import kotlinx.serialization.Serializable

@Serializable
data class LayoutConfig(var splitAmount: Float=0f, var hidden: Boolean=false, val splitType: String="horizontal", val activePanel: String="default")