
#include "precision.slsl:precision"

#include "attributes.slsl:FS"

#include "uniforms.slsl:u_diffuseTexture"

#include "uniforms.slsl:u_normalTexture"

#include "uniforms.slsl:u_specularTexture"

void main() {

	vec4 diffuseColor = texture2D(u_diffuseTexture, v_texCoord0);
	vec4 normalColor = texture2D(u_normalTexture, v_texCoord0);
	vec4 specularColor = texture2D(u_specularTexture, v_texCoord0);

	vec3 normal = normalize(normalColor.xyz * 2.0 - 1.0);
	vec3 lightDir = normalize(vec3(0.0, 0.0, 1.0));

	float diff = max(dot(normal, lightDir), 0.0);
	vec3 diffuse = diff * diffuseColor.rgb;

	vec3 viewDir = normalize(vec3(0.0, 0.0, 1.0));
	vec3 reflectDir = reflect(-lightDir, normal);
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32.0);
	vec3 specular = spec * specularColor.rgb;

	vec4 finalColor = vec4(diffuse + specular, 1.0);

	gl_FragColor = vec4(v_color.rgb,1.0);

}