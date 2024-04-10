package org.yunghegel.salient.editor.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.PopupMenu

class ViewportContextMenu : PopupMenu() {

    var translate: EditorMenuItem? = null
    var rotate: EditorMenuItem? = null
    var scale: EditorMenuItem? = null
    var delete: EditorMenuItem? = null
    var duplicate: EditorMenuItem? = null
    var primitiveMenu: PopupMenu? = null
    var meshMenu: PopupMenu? = null
    var cube: EditorMenuItem? = null
    var sphere: EditorMenuItem? = null
    var cylinder: EditorMenuItem? = null
    var plane: EditorMenuItem? = null
    var torus: EditorMenuItem? = null
    var cone: EditorMenuItem? = null
    var capsule: EditorMenuItem? = null
    var arrow: EditorMenuItem? = null
    var pointLight: EditorMenuItem? = null
    var directionalLight: EditorMenuItem? = null
    var spotLight: EditorMenuItem? = null
    var areaLight: EditorMenuItem? = null
    var particleEmitter: EditorMenuItem? = null
    var splitEdges: EditorMenuItem? = null
    var joinFace: EditorMenuItem? = null
    var subdivide: EditorMenuItem? = null
    var extrude: EditorMenuItem? = null

    init {
        createMenuItems()
        createMenuListeners()
    }

    fun attachListener(actor:Actor) {
        actor.addListener(object : ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if(!isOver) return true
                if (button == 1) {
                    showMenu(actor.stage, x, y)
                }
                return super.touchDown(event, x, y, pointer, button)
            }

        })
    }

    private fun createMenuListeners() {
        translate!!.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {

            }

        })

        rotate!!.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {

            }

        })

        scale!!.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {

            }

        })

        cube!!.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {

            }

        })

        sphere!!.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {

            }
        })

        cylinder!!.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {

            }
        })

        cone!!.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {

            }
        })

        capsule!!.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {

            }
        })

        splitEdges!!.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {

            }
        })
    }

    private fun createMenuItems() {
        translate = EditorMenuItem("Translate")
        rotate = EditorMenuItem("Rotate")
        scale = EditorMenuItem("Scale")
        delete = EditorMenuItem("Delete")
        duplicate = EditorMenuItem("Duplicate")
        val transformGroup = EditorMenuItem("Transform")
        val transformMenu = PopupMenu()
        transformMenu.addItem(translate)
        transformMenu.addItem(rotate)
        transformMenu.addItem(scale)
        transformGroup.subMenu = transformMenu
        addItem(transformGroup)

        primitiveMenu = PopupMenu()
        val primitiveGroup = EditorMenuItem("Add Primitive")
        cube = EditorMenuItem("Cube")
        sphere = EditorMenuItem("Sphere")
        cylinder = EditorMenuItem("Cylinder")
        plane = EditorMenuItem("Plane")
        torus = EditorMenuItem("Torus")
        cone = EditorMenuItem("Cone")
        capsule = EditorMenuItem("Capsule")
        arrow = EditorMenuItem("Arrow")

        primitiveMenu!!.addItem(cube)
        primitiveMenu!!.addItem(sphere)
        primitiveMenu!!.addItem(cylinder)
        primitiveMenu!!.addItem(cone)
        primitiveMenu!!.addItem(capsule)
        primitiveMenu!!.addItem(arrow)

        primitiveGroup.subMenu = primitiveMenu

        meshMenu = PopupMenu()
        val meshGroup = EditorMenuItem("Mesh")
        splitEdges = EditorMenuItem("Split Edges")
        joinFace = EditorMenuItem("Join Faces")
        subdivide = EditorMenuItem("Subdivide")
        extrude = EditorMenuItem("Extrude")

        meshMenu!!.addItem(splitEdges)
        meshMenu!!.addItem(joinFace)
        meshMenu!!.addItem(subdivide)
        meshMenu!!.addItem(extrude)

        meshGroup.subMenu = meshMenu

        addItem(meshGroup)

        addItem(primitiveGroup)

        addSeparator()
        addItem(delete)
        addItem(duplicate)
    }
}