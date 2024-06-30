package org.yunghegel.salient.engine.input

enum class Control {
    PAN, ROTATE, ZOOM, HOME, RESET;

    companion object {
        fun mask(vararg controls: Control): Int {
            var mask = 0
            for (control in controls) {
                mask = mask or (1 shl control.ordinal)
            }
            return mask
        }

    }
}