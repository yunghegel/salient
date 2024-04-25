package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.files.FileHandle;

public class ShaderPart {
    /** stage in the pipeline */
    final ShaderStage stage;
    /** original shader source code */
    public final String source;
    /** shader handle */
    int handle;
    /** final shader code (prepended code + original source code) */
    public String finalCode;
    /**
     * @param stage shader stage
     * @param source shader source code
     */
    public ShaderPart (ShaderStage stage, String source) {
        this.stage = stage;
        this.source = source;
    }


}