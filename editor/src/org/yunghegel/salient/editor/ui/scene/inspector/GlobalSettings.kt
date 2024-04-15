package org.yunghegel.salient.editor.ui.scene.inspector

import org.yunghegel.gdx.utils.ext.padVertical
import org.yunghegel.gdx.utils.reflection.FieldAccessor
import org.yunghegel.gdx.utils.ui.ActorList
import org.yunghegel.salient.editor.app.configs.Settings
import org.yunghegel.salient.engine.helpers.Grid
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.system.props
import org.yunghegel.salient.engine.ui.layout.CollapsePanel
import org.yunghegel.salient.engine.ui.search.SearchBar
import org.yunghegel.salient.engine.ui.widgets.value.DefaultFieldEditor
import org.yunghegel.salient.engine.ui.widgets.value.ReflectionBasedEditor

class GlobalSettings : BaseInspector("Global","config"), SearchBar.SearchProvider<FieldAccessor,DefaultFieldEditor> {

    private var editors = mutableListOf<ReflectionBasedEditor>()

    private val searchBar = SearchBar(this)

    override fun createLayout() {
        val grid : Grid = inject()
        val config = grid.config
        addEditor(config,"Grid")

        with (Settings.i) {
            addEditor(graphics.window,"Window")
            addEditor(graphics.opengl,"OpenGL")
            addEditor(graphics.video,"Video")

        }




        createTitleActor(searchBar)
//        collectSearchable().forEach { println(it.searchTerms) }
    }

    fun addEditor(obj: Any, category: String) {
        val editor = ReflectionBasedEditor(obj,category)
        val container = CollapsePanel(category,null,editor)
        add(container).growX().padVertical(5f).row()
        editors.add(editor)
    }

    override val term: () -> String = {
        searchBar.search.textField.text ?: ""
    }

    var previousSearchResults = mutableListOf<FieldAccessor>()

    override fun rebuildList(list: ActorList, searchables: List<FieldAccessor>) {
        list.items.clear()
        list.setItems(searchables.map { it.actor!! }.toTypedArray()) {
            height(20f)
            growX()
        }
        previousSearchResults = searchables.toMutableList()
    }

    override fun search(query: String, searchables: List<FieldAccessor>): List<FieldAccessor> {
        if (query.isBlank()) return emptyList()
        val results = mutableListOf<FieldAccessor>()

        searchables.forEach { searchable ->
            searchable.searchTerms.forEach { term ->
                if(term.contains(query,true)) {
                    if(!results.contains(searchable)) results.add(searchable)
                }
            }
        }
        return results
    }

    override fun collectSearchable(): List<FieldAccessor> {
        val searchables = mutableListOf<FieldAccessor>()
        props.forEach { (_, value) ->
            searchables.add(value)
        }

        return searchables
    }

}