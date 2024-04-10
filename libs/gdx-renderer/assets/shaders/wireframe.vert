in vec3 a_position;
in vec4 a_color;
in vec2 a_texCoord0;

varying vec4 v_color;
varying vec2 v_texCoords;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;

void main()
{
    gl_Position =  vec4(a_position, 1.0);
}

