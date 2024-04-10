#include "attributes.slsl:VS"
#include "uniforms.slsl:u_projViewTrans"
#include "uniforms.slsl:u_worldTrans"



void main() {
	passPosition();
	passNormal();
	passTexCoord0();
	passColor();
	vec4 worldPos = u_worldTrans * a_position;
	gl_Position = u_projViewTrans * worldPos;
}