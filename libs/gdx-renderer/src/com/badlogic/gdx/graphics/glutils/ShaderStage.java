package com.badlogic.gdx.graphics.glutils;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL32;
public class ShaderStage {

    public static final ShaderStage vertex = new ShaderStage(GL20.GL_VERTEX_SHADER, "Vertex", ".vert");
    public static final ShaderStage fragment = new ShaderStage(GL20.GL_FRAGMENT_SHADER, "Fragment", ".frag");
    public static final ShaderStage geometry = new ShaderStage(GL32.GL_GEOMETRY_SHADER, "Geometry", ".geom");

    public static final ShaderStage [] stages = {vertex, fragment, geometry};

    public final int type;
    public final String name;
    public final String suffix;

    /** code that is always added to shader code for this stage, typically used to inject a #version line. Note that this is added
     * as-is, you should include a newline (`\n`) if needed. */
    public String prependCode;

    public ShaderStage (int type, String name, String suffix) {
        super();
        this.type = type;
        this.name = name;
        this.suffix = suffix;
    }
}
