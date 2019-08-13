#version 330

uniform vec3 direction;
uniform vec3 diffuseColor;
uniform sampler2D shadowMap;
uniform mat4 shadowmapVPMatrix;
uniform vec3 shadowCameraPos;
uniform float zFar;

#include <GBuffers.include.glsl>

vec3 calculateDiffuse(vec3 facenormal) {
	float directiondiff = max(dot(-direction, facenormal), 0.0);
	vec3 diffusecolor = diffuseColor * directiondiff;
	
	return diffusecolor;
}

float calculateShadowAmount(vec3 worldspaceposition, vec3 facenormal) {
	vec4 clipspaceposition = shadowmapVPMatrix * vec4(worldspaceposition, 1.0);
	if (clipspaceposition.x < -1 ||  clipspaceposition.x > 1 || clipspaceposition.y < -1 || clipspaceposition.y > 1)
		return 0;
	vec4 shadowUV = (clipspaceposition + 1.0) / 2.0;
	
	vec3 topixel = worldspaceposition - shadowCameraPos;
	float actualDist = length(topixel);
	
	float bias = max(0.006 * (1.0 - dot(facenormal, topixel)), 0.000);
	float z = texture(shadowMap, shadowUV.xy).r;
	z *= zFar;
	z += 0.1;
	
	float shadow = actualDist - bias > z ? 0.0 : 1.0;

	float distfromcenter = max(abs(clipspaceposition.x), abs(clipspaceposition.y));
	float visibility = 	max((distfromcenter - 0.9) * 10.0, 0.0);
	
	return clamp(shadow + visibility, 0.0, 1.0);
}

void main(void) {
	float specularity, roughness, stencil;
	readSpecularMap(textureCoord, specularity, roughness, stencil);
	
	// TODO: Move to stencil buffer
	if (stencil < 0.5) // Skip if stencil says so
		discard;
		
	vec3 normal = getNormal(textureCoord);
	vec3 diffuse = calculateDiffuse(normal);
	
	vec4 cameraspaceposition = texture(positionTexture, textureCoord);
	vec3 worldspaceposition = cameraspaceposition.xyz + campos;
	float shadowAmount = calculateShadowAmount(worldspaceposition, normal);
		
	pixelColor = vec4(diffuse * shadowAmount, 1.0);
}