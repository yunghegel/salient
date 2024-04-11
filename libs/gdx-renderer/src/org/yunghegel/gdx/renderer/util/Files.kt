package org.yunghegel.gdx.renderer.util

import com.badlogic.gdx.Gdx

fun String.toInternalFile() = Gdx.files.internal(this)

fun String.toLocalFile() = Gdx.files.local(this)

fun String.toExternalFile() = Gdx.files.external(this)

fun String.readFile() = Gdx.files.internal(this).readString()