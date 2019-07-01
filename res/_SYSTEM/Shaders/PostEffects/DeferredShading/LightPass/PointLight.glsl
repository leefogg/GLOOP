#version 330

uniform vec3 
	position, 
	color;
uniform float
	quadraticAttenuation;

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
}