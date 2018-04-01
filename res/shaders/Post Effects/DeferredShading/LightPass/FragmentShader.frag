#version 400 core

in vec2 textureCoord;

uniform sampler2D 
	positionTexture,
	normalTexture,
	specularTexture;
	
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

uniform vec3 ambientLight = vec3(0);
uniform int numberOfPointLights = 0;
uniform PointLight pointLights[64];
uniform int numberOfDirectionalLights = 0;
uniform DirectionalLight directionalLights[8];
uniform int numberOfSpotLights;
uniform SpotLight spotLights[32];
uniform vec3 fogColor = vec3(0,0,0);
uniform float fogDensity = 0.02;

uniform float znear, zfar;
uniform vec3 campos;
uniform mat4 InverseVPMatrix;
uniform mat4 VPMatrix;

const float MaxSpecularExponent = 256.0;

uniform float time;

layout(location=4) out vec3 pixelColor;

float calculateDiffuse(vec3 facenormal, vec3 worldspaceposition, vec3 position) {
	vec3 tolightdir = normalize(position - worldspaceposition);
	float directiondiff = max(dot(facenormal, tolightdir), 0);
	
	return directiondiff;
}

float calulateLuminosity(float distance, float quadraticattenuation) {
	float attenuation = 1.0 + quadraticattenuation * (distance * distance);
	float luminosity = 1.0 / attenuation;
	
	return luminosity;
}

// Point lights
vec3 calculateDiffuse(vec3 facenormal, vec3 worldspaceposition, PointLight pointlight) {
	return pointlight.color * calculateDiffuse(facenormal, worldspaceposition, pointlight.position);
}

vec3 calculateSpecular(vec3 worldspaceposition, vec3 facenormal, vec3 lightposition, vec3 lightcolor, float specularity, float exponent, float quadraticattenuation) {
	vec3 lightdir = worldspaceposition - lightposition;
	vec3 viewdir = campos - worldspaceposition;
	float distance = abs(length(lightdir + viewdir));
	float luminosity = calulateLuminosity(distance, quadraticattenuation);
	
	lightdir = normalize(lightdir);
	viewdir = normalize(viewdir);
	vec3 reflectdir = reflect(lightdir, facenormal);
	
	float specularcontribution = max(dot(viewdir, reflectdir), 0.0);
	float spec = pow(specularcontribution, max(exponent, 1));
	return spec * specularity * luminosity * lightcolor;
}

float calulateLuminosity(vec3 lightpos, vec3 worldpos, float quadraticAttenuation) {
	float distancetolight = length(lightpos - worldpos);
	return calulateLuminosity(distancetolight, quadraticAttenuation);
}
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

// Used for dithering
#define MOD3 vec3(443.8975,397.2973, 491.1871)
float hash12(vec2 p) {
	vec3 p3  = fract(vec3(p.xyx) * MOD3);
    p3 += dot(p3, p3.yzx + 19.19);
    return fract((p3.x + p3.y) * p3.z);
}
vec3 dither(vec3 color, vec2 texcoord) {
	vec2 seed = texcoord;
	seed += fract(time);

	vec3 rnd = vec3(hash12( seed ) + hash12(seed + 0.59374) - 0.5 );

	return color + rnd/255.0;
}

void main(void) {
	vec4 specularmap = texture(specularTexture, textureCoord);
	float specularity = specularmap.r;
	float roughness = specularmap.g;
	float stencil = specularmap.b;
	
	// TODO: Move to stencil buffer
	if (stencil < 0.5) // Skip if stencil says so
		discard;
		
	
	vec4 normalbuffer = texture(normalTexture, textureCoord);
	vec3 facenormal = normalbuffer.rgb * 2.0 - 1.0;
	facenormal = normalize(facenormal);
	
	vec4 cameraspaceposition = texture(positionTexture, textureCoord);
	vec3 worldspaceposition = cameraspaceposition.xyz + campos;
	
	
	// Ambiance
	vec3 ambientcolor = ambientLight;
	
	pixelColor = ambientcolor;
	
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
		pixelColor += diffusecolor + specularcolor;
	}
	
	// Directional lights
	for (int i=0; i<numberOfDirectionalLights; i++) {
		DirectionalLight directionallight = directionalLights[i];
		vec3 diffusecolor = calculateDiffuse(facenormal, directionallight);
		pixelColor += diffusecolor;
	}
	
	// Spot lights
	for (int i=0; i<numberOfSpotLights; i++) {
		SpotLight light = spotLights[i];
		
		// Diffuse
		vec3 diffusecolor = calculateDiffuse(worldspaceposition, facenormal, light);
		
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
		float luminosity = calulateLuminosity(worldspaceposition, light);
		
		diffusecolor *= luminosity;
		// Specular has luminosity built in
		pixelColor += diffusecolor + specularcolor;
	}
	
	//TODO: Change to Blue Noise
	#if defined DITHER
	pixelColor = dither(pixelColor, textureCoord);
	#endif
	
	#if defined FOG
	float dist = abs(length(campos - worldspaceposition));
	float fogfactor = 1.0 / exp(dist * fogDensity);
	fogfactor = clamp(fogfactor, 0.0, 1.0);
	
	pixelColor = mix(fogColor, pixelColor, fogfactor);
	#endif
}