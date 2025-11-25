#include "attributes.slsl:VS"
#include "uniforms.slsl:u_projViewTrans"
#include "uniforms.slsl:u_worldTrans"
#include "types.slsl:point_light"
#include "types.slsl:spot_light"
#include "types.slsl:dir_light"

uniform DirectionalLight u_directionalLight;

uniform SpotLight u_spotLight[3];

uniform PointLight u_pointLight[3];

void main() {
	passPosition();
	passNormal();
	passTexCoord0();
	vec4 worldPos = u_worldTrans * a_position;
	gl_Position = u_projViewTrans * worldPos;
}