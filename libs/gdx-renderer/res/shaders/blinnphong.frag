#include "attributes.slsl:FS"
#include "types.slsl:dir_light"
varying vec3 v_viewDir;

uniform DirectionalLight u_dir_light;

void main() {
    vec3 col = calcDirLight(u_dir_light, v_normal,vec3(v_viewDir.xyz));

    gl_FragColor = vec4(col,1.0);
}