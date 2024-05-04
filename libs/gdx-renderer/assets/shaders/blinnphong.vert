#include "attributes.slsl:VS"
#include "uniforms.slsl:u_projViewTrans"
#include "uniforms.slsl:u_worldTrans"
#include "uniforms.slsl:u_cameraPosition"


varying vec3 v_viewDir;

void main() {
    #ifdef colorFlag
    passColor();
    #endif

    #ifdef normalFlag
    passNormal();
    #endif

    #ifdef texCoord0Flag
    passTexCoord0();
    #endif

    v_viewDir = u_cameraPosition.xyz - a_position.xyz;


    passPosition();
    vec4 worldPos = u_worldTrans * a_position;
    gl_Position = u_projViewTrans * worldPos;
}