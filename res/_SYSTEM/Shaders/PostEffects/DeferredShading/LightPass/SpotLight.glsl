#version 330

uniform vec3 
	position, 
	direction, 
	color;
uniform float 
	innerCone, 
	outerCone,
	quadraticAttenuation,
	VolumetricLightStrength = 2.0;
uniform sampler2D shadowMap;
uniform mat4 shadowmapVPMatrix;
uniform float zFar;

#include <GBuffers.include.glsl>
#include <Diffuse.include.glsl>
#include <Luminosity.include.glsl>
float calulateLuminosity(vec3 worldspaceposition) {
	float luminosity = calulateLuminosity(position, worldspaceposition, quadraticAttenuation);
	luminosity *= 1.0 + ((outerCone + 1.0) / 2.0);
	
	return luminosity;
}
#include <Specular.include.glsl>

vec3 calculateDiffuse(vec3 worldspaceposition, vec3 facenormal) {
	vec3 tolightdir = normalize(position - worldspaceposition);
	float theta = dot(-tolightdir, direction);
	
	float epsilon   = innerCone - outerCone;
	float intensity = clamp((theta - outerCone) / epsilon, 0.0, 1.0);
	float diffuse = calculateDiffuse(facenormal, worldspaceposition, position);
	
	return color * intensity * diffuse;
}

float calculateShadowAmount(vec3 worldspaceposition, vec3 facenormal) {
	vec4 clipspaceposition = shadowmapVPMatrix * vec4(worldspaceposition, 1.0);
	clipspaceposition /= clipspaceposition.w;
	if (clipspaceposition.x < -1 ||  clipspaceposition.x > 1 || clipspaceposition.y < -1 || clipspaceposition.y > 1)
		return 1.0;
	vec4 shadowUV = (clipspaceposition + 1.0) / 2.0;
	
	vec3 topixel = worldspaceposition - position;
	float actualDist = length(topixel);
	
	float bias = max(0.006 * (1.0 - dot(facenormal, topixel)), 0.000);
	float z = texture(shadowMap, shadowUV.xy).r;
	z *= zFar;
	z += 0.1;
	
	float shadow = actualDist - bias > z ? 0.0 : 1.0;

	float distfromcenter = max(abs(clipspaceposition.x), abs(clipspaceposition.y));
	float visibility = max((distfromcenter - 0.9) * 10.0, 0.0);
	
	return clamp(shadow + visibility, 0.0, 1.0);
}

void main(void) {
	float specularity, roughness, stencil;
	readSpecularMap(textureCoord, specularity, roughness, stencil);
	
	// TODO: Move to stencil buffer
	if (stencil < 0.5) // Skip if stencil says so
		discard;
	
	vec3 facenormal = getNormal(textureCoord);
	
	vec4 cameraspaceposition = texture(positionTexture, textureCoord);
	vec3 worldspaceposition = cameraspaceposition.xyz + campos;
	
	// Diffuse
	vec3 diffusecolor = calculateDiffuse(worldspaceposition, facenormal);
	
	// Specular
	// TODO: Bug: Spot lights show specular when light isn't amimed at point
	vec3 specularcolor = calculateSpecular(
		worldspaceposition, 
		facenormal, 
		position, 
		color, 
		specularity,
		roughness * MaxSpecularExponent,
		quadraticAttenuation
	);
	
	// Attenuation
	float luminosity = calulateLuminosity(worldspaceposition);
	
	vec3 halo;
	#if defined VOLUMETRICLIGHTING
	// Volumetric Lighting
	int steps = 16;
	vec3 stepsize = vec3(worldspaceposition - campos) / steps;
	vec3 point = campos + stepsize * rand(textureCoord * time);
	for (int step=0; step<steps-2; step++){
		halo += calculateDiffuse(point, facenormal);
		
		point += stepsize;
	}
	halo /= steps;
	halo *= VolumetricLightStrength;
	#endif
	
	float shadowAmount = calculateShadowAmount(worldspaceposition, facenormal);
	
	diffusecolor *= luminosity;
	diffusecolor *= shadowAmount;
	// Specular has luminosity built in
	pixelColor = vec4(diffusecolor + specularcolor + halo, 1.0);
}