package types

val BaseTest = lwjgl3test("None") {

    execCreate = {

    }

    execRender = {
        bundle.render()
    }

}