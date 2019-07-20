#version 330

uniform vec3 
	position, 
	color;
uniform float
	quadraticAttenuation;
uniform samplerCube depthMap;

#include <GBuffers.include.glsl>
#include <Diffuse.include.glsl>
vec3 calculateDiffuse(vec3 facenormal, vec3 worldspaceposition) {
	return color * calculateDiffuse(facenormal, worldspaceposition, position);
}
#include <Luminosity.include.glsl>
#include <Specular.include.glsl>

vec3 pointLighting(float specularity, float roughness, vec3 facenormal, vec3 worldspaceposition) {
	// Diffuse
	vec3 diffusecolor = calculateDiffuse(facenormal, worldspaceposition);
	
	// Specular
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
	float luminosity = calulateLuminosity(
		position,
		worldspaceposition,
		quadraticAttenuation
	);
	
	diffusecolor *= luminosity;
	// Specular has luminosity built in
	return diffusecolor + specularcolor;
}

float getShadowAmount(vec3 worldspaceposition, vec3 facenormal) {
	float zfar = 150;
	
	vec3 topixel =  worldspaceposition - position;
	float shadowmap = texture(depthMap, topixel).r;
	float actualdepth = length(topixel);
	actualdepth /= zfar;
	
	float bias = max(0.001 * (1.0 - dot(facenormal, topixel)), 0.000);  
	
	return actualdepth - bias > shadowmap ? 0.0 : 1.0;
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
	
	pixelColor = vec4(pointLighting(specularity, roughness, facenormal, worldspaceposition), 1.0);
	pixelColor *= vec4(vec3(getShadowAmount(worldspaceposition, facenormal)), 1.0);
}