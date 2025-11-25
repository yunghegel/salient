
#include "precision.slsl:precision"
#include "attributes.slsl:FS"

#include "uniforms.slsl:u_diffuseTexture"
#include "uniforms.slsl:u_normalTexture"
#include "uniforms.slsl:u_specularTexture"
#include "uniforms.slsl:u_cameraPosition"
#include "uniforms.slsl:u_worldTrans"
#include "uniforms.slsl:u_normalMatrix"
#include "uniforms.slsl:u_shininess"
#include "types.slsl:point_light"
#include "types.slsl:spot_light"
#include "types.slsl:dir_light"

uniform DirectionalLight u_directionalLight;

uniform SpotLight u_spotLight[3];

uniform PointLight u_pointLight[3];


void main() {
	// Sample textures with fallbacks
	vec4 diffuseColor = vec4(1.0, 1.0, 1.0, 1.0); // Default white
	vec4 normalColor = vec4(0.5, 0.5, 1.0, 1.0);  // Default normal (0,0,1) in tangent space
	vec4 specularColor = vec4(1.0, 1.0, 1.0, 1.0); // Default white specular
	
	#ifdef diffuseTextureFlag
		diffuseColor = texture2D(u_diffuseTexture, v_texCoord0);
	#endif
	
	#ifdef normalTextureFlag
		normalColor = texture2D(u_normalTexture, v_texCoord0);
	#endif
	
	#ifdef specularTextureFlag
		specularColor = texture2D(u_specularTexture, v_texCoord0);
	#endif

	// Transform normal from tangent space to world space
	vec3 tangentNormal = normalize(normalColor.xyz * 2.0 - 1.0);
	vec3 normal = normalize(u_normalMatrix * tangentNormal);
	
	// Calculate world position and view direction
	vec3 fragPos;
	vec3 viewDir;
	#ifdef positionFlag
		// If position is available, calculate world position and view direction
		vec4 worldPos = u_worldTrans * v_position;
		fragPos = worldPos.xyz;
		viewDir = normalize(u_cameraPosition.xyz - fragPos);
	#else
		// Fallback: use camera position as fragment position and fixed view direction
		fragPos = u_cameraPosition.xyz;
		viewDir = normalize(vec3(0.0, 0.0, 1.0));
	#endif
	
	// Initialize result color with minimal ambient lighting
	vec3 result = diffuseColor.rgb * 0.1; // Base ambient lighting
	
	// Apply directional light
	result += calcDirLight(u_directionalLight, normal, viewDir) * diffuseColor.rgb;
	
	// Apply point lights
	for(int i = 0; i < 3; i++) {
		result += calcPointLight(u_pointLight[i], normal, fragPos, viewDir) * diffuseColor.rgb;
	}
	
	// Apply spot lights
	for(int i = 0; i < 3; i++) {
		result += calcSpotLight(u_spotLight[i], normal, fragPos, viewDir) * diffuseColor.rgb;
	}
	
	// Final color
	gl_FragColor = vec4(result, diffuseColor.a);
}