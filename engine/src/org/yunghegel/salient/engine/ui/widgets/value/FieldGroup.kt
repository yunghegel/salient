package org.yunghegel.salient.engine.ui.widgets.value

import org.yunghegel.gdx.utils.reflection.Accessor
import org.yunghegel.gdx.utils.data.Scope
import org.yunghegel.gdx.utils.data.Searchable
import org.yunghegel.gdx.utils.reflection.FieldAccessor

class FieldGroup(val name:String, val fields:MutableList<FieldAccessor> = mutableListOf()) :  Scope, Searchable {

    override val scope: String = name
    override val searchTerms: List<String> = fields.map { it.getName() }
}