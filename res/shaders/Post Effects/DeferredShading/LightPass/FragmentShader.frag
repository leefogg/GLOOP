#version 400 core

in vec2 textureCoord;

uniform sampler2D 
	positionTexture,
	normalTexture,
	specularTexture;
	
struct PointLight {
	vec3 position;
	vec3 color;
	float linearAttenuation;
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
	float linearAttenuation;
	float quadraticAttenuation;
};

uniform vec3 ambientLight = vec3(0);
uniform int numberOfPointLights = 0;
uniform PointLight pointLights[64];
uniform int numberOfDirectionalLights = 0;
uniform DirectionalLight directionalLights[8];
uniform int numberOfSpotLights;
uniform SpotLight spotLights[32];

uniform float znear, zfar;
uniform vec3 campos;
uniform mat4 InverseVPMatrix;

const float MaxSpecularExponent = 256;

uniform float time;

layout(location=4) out vec3 pixelColor;

float calculateDiffuse(vec3 facenormal, vec3 worldspaceposition, vec3 position) {
	vec3 tolightdir = normalize(position - worldspaceposition);
	float directiondiff = max(dot(facenormal, tolightdir), 0);
	
	return directiondiff;
}

// Point lights
vec3 calculateDiffuse(vec3 facenormal, vec3 worldspaceposition, PointLight pointlight) {
	return pointlight.color * calculateDiffuse(facenormal, worldspaceposition, pointlight.position);
}

vec3 calculateSpecular(vec3 worldspaceposition, vec3 facenormal, vec3 lightposition, vec3 lightcolor, float specularity, float exponent) {
	vec3 lightdir = normalize(worldspaceposition - lightposition);
	vec3 viewdir = normalize(campos - worldspaceposition);
	vec3 reflectdir = reflect(lightdir, facenormal);
	
	float specularcontribution = max(dot(viewdir, reflectdir), 0.0);
	float spec = pow(specularcontribution, max(exponent, 0.001));
	return spec * specularity * lightcolor;
}

float calulateLuminosity(vec3 worldspaceposition, PointLight light) {
	float distancetolight = length(light.position - worldspaceposition);
	float attenuation = 1.0 + light.linearAttenuation * distancetolight + light.quadraticAttenuation * (distancetolight * distancetolight);
	float luminosity = 1.0 / attenuation;
	
	return luminosity;
}
float calulateLuminosity(vec3 worldspaceposition, SpotLight light) {
	float distancetolight = length(light.position - worldspaceposition);
	float attenuation = 1.0 + light.linearAttenuation * distancetolight + light.quadraticAttenuation * (distancetolight * distancetolight);
	float luminosity = 1.0 / attenuation;
	luminosity *= 1.0 + ((light.outerCone + 1.0) / 2.0);
	
	return luminosity;
}

// Directional lights
vec3 calculateDiffuse(vec3 facenormal, DirectionalLight directionallight) {
	float directiondiff = max(dot(directionallight.direction, facenormal), 0);
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

vec3 getWorldSpaceCoords() {
	vec4 vertposition = texture(positionTexture, textureCoord);
	vertposition.xy = (textureCoord * 2.0 - 1.0);
	vertposition.w = 1.0;
	vec4 cameraspaceposition = InverseVPMatrix * vertposition;
	vec3 worldspaceposition = cameraspaceposition.xyz / cameraspaceposition.w;
	
	return worldspaceposition;
}

vec3 calcWorldPosition(float depth, vec3 view_ray, vec3 cam_position) {
	view_ray = normalize(view_ray);
	return view_ray * depth - cam_position;
}

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
	if (specularmap.z < 0.5)
		discard;
	float specularity = specularmap.r;
	float specularexponent = specularmap.g;
	float roughness = specularmap.b;
	
	vec4 normalbuffer = texture(normalTexture, textureCoord);
	vec3 facenormal = normalbuffer.rgb * 2.0 - 1.0;
	facenormal = normalize(facenormal);
	
	vec4 cameraspaceposition = texture(positionTexture, textureCoord);
	vec3 worldspaceposition = cameraspaceposition.xyz + campos;
	//vec3 worldspaceposition = calcWorldPosition(cameraspaceposition.a, viewRay, campos);
	
	
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
			specularexponent * MaxSpecularExponent
		);
		
		// Attenuation
		float luminosity = calulateLuminosity(worldspaceposition, light);
		
		diffusecolor *= luminosity;
		specularcolor *= luminosity;
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
			specularexponent * MaxSpecularExponent
		);
		
		// Attenuation
		float luminosity = calulateLuminosity(worldspaceposition, light);
		
		diffusecolor *= luminosity;
		specularcolor *= luminosity;
		
		pixelColor += specularcolor;
	}
	
	#if defined DITHER
	pixelColor = dither(pixelColor, textureCoord);
	#endif
}