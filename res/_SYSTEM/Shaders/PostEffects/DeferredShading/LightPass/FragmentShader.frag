#version 330
	
struct PointLight {
	vec3 position;
	vec3 color;
	float quadraticAttenuation;
};
struct DirectionalLight {
	vec3 direction;
	vec3 diffuseColor;
};
struct SpotLight {
	vec3  position;
	vec3  direction;
	vec3  color;
	float innerCone;
	float outerCone;
	float quadraticAttenuation;
};

uniform int numberOfPointLights = 0;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform int numberOfDirectionalLights = 0;
uniform DirectionalLight directionalLights[MAX_DIRECTIONAL_LIGHTS];
uniform int numberOfSpotLights;
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];
uniform float VolumetricLightStrength = 2.0;

#include <GBuffers.include.glsl>

#include <Diffuse.include.glsl>


// Point lights
vec3 calculateDiffuse(vec3 facenormal, vec3 worldspaceposition, PointLight pointlight) {
	return pointlight.color * calculateDiffuse(facenormal, worldspaceposition, pointlight.position);
}

#include <Luminosity.include.glsl>

#include <Specular.include.glsl>

float calulateLuminosity(vec3 worldspaceposition, SpotLight light) {
	float luminosity = calulateLuminosity(light.position, worldspaceposition, light.quadraticAttenuation);
	luminosity *= 1.0 + ((light.outerCone + 1.0) / 2.0);
	
	return luminosity;
}

// Directional lights
vec3 calculateDiffuse(vec3 facenormal, DirectionalLight directionallight) {
	float directiondiff = max(dot(directionallight.direction, facenormal), 0.0);
	vec3 diffusecolor = directionallight.diffuseColor * directiondiff;
	
	return diffusecolor;
}

// Spot lights
vec3 calculateDiffuse(vec3 worldspaceposition, vec3 facenormal, SpotLight spotlight) {
	vec3 tolightdir = normalize(spotlight.position - worldspaceposition);
	float theta = dot(-tolightdir, spotlight.direction);
	
	float epsilon   = spotlight.innerCone - spotlight.outerCone;
	float intensity = clamp((theta - spotlight.outerCone) / epsilon, 0.0, 1.0);
	float diffuse = calculateDiffuse(facenormal, worldspaceposition, spotlight.position);
	
	return spotlight.color * intensity * diffuse;
}

#include <Dither.include.glsl>

void main(void) {
	float specularity, roughness, stencil;
	readSpecularMap(textureCoord, specularity, roughness, stencil);
	
	// TODO: Move to stencil buffer
	if (stencil < 0.5) // Skip if stencil says so
		discard;
		
	vec3 facenormal = getNormal(textureCoord);
	
	vec4 cameraspaceposition = texture(positionTexture, textureCoord);
	vec3 worldspaceposition = cameraspaceposition.xyz + campos;
	
	
	// Ambiance
	vec3 ambientcolor = vec3(0);
	
	vec3 pixelcolor = ambientcolor;
	
	// Point Lights
	for (int i=0; i<numberOfPointLights; i++) {
		PointLight light = pointLights[i];
		
		// Diffuse
		vec3 diffusecolor = calculateDiffuse(facenormal, worldspaceposition, light);
		
		// Specular
		vec3 specularcolor = calculateSpecular(
			worldspaceposition, 
			facenormal, 
			light.position, 
			light.color, 
			specularity,
			roughness * MaxSpecularExponent,
			light.quadraticAttenuation
		);
		
		// Attenuation
		float luminosity = calulateLuminosity(light.position, worldspaceposition, light.quadraticAttenuation);
		
		diffusecolor *= luminosity;
		// Specular has luminosity built in
		pixelcolor += diffusecolor + specularcolor;
	}
	
	// Directional lights
	for (int i=0; i<numberOfDirectionalLights; i++) {
		DirectionalLight directionallight = directionalLights[i];
		vec3 diffusecolor = calculateDiffuse(facenormal, directionallight);
		pixelcolor += diffusecolor;
	}
	
	// Spot lights
	for (int i=0; i<numberOfSpotLights; i++) {
		SpotLight light = spotLights[i];
		
		// Diffuse
		vec3 diffusecolor = calculateDiffuse(worldspaceposition, facenormal, light);
		
		// Specular
		// TODO: Bug: Spot lights show specular when light isn't amimed at point
		vec3 specularcolor = calculateSpecular(
			worldspaceposition, 
			facenormal, 
			light.position, 
			light.color, 
			specularity,
			roughness * MaxSpecularExponent,
			light.quadraticAttenuation
		);
		
		// Attenuation
		float luminosity = calulateLuminosity(worldspaceposition, light);
		
		vec3 halo;
		#if defined VOLUMETRICLIGHTING
		// Volumetric Lighting
		int steps = 16;
		vec3 stepsize = vec3(worldspaceposition - campos) / steps;
		vec3 point = campos + stepsize * rand(textureCoord * time);
		for (int step=0; step<steps-2; step++){
			halo += calculateDiffuse(point, facenormal, light);
			
			point += stepsize;
		}
		halo /= steps;
		halo *= VolumetricLightStrength;
		#endif
		
		
		diffusecolor *= luminosity;
		// Specular has luminosity built in
		pixelcolor += diffusecolor + specularcolor + halo;
	}
	
	pixelColor = vec4(pixelcolor, 1.0);
}