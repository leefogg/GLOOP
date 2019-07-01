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
	
	
	diffusecolor *= luminosity;
	// Specular has luminosity built in
	pixelColor = vec4(diffusecolor + specularcolor + halo, 1.0);
}