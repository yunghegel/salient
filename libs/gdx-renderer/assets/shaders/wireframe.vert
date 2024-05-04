in vec3 a_position;
in vec4 a_color;
in vec2 a_texCoord0;

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec3 v_position;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;

void main()
{
    v_position = (u_worldTrans * vec4(a_position, 1.0)).xyz;
    gl_Position =  vec4(a_position, 1.0);
}

