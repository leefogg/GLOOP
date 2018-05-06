#version 150

in vec2 textureCoord;
in vec3 viewRay;

uniform sampler2D 
	albedoTexture,
	PositionTexture,
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

uniform float SpecularExponent = 16;

out vec3 pixelColor;

vec3 calulateAmbience(vec4 albedocolor) {
	return albedocolor.rgb * ambientLight;
}

float calculateDiffuse(vec3 facenormal, vec3 worldspaceposition, vec3 position) {
	vec3 tolightdir = normalize(position - worldspaceposition);
	float directiondiff = max(dot(facenormal, tolightdir), 0);
	
	return directiondiff;
}

// Point lights
vec3 calculateDiffuse(vec3 facenormal, vec3 worldspaceposition, PointLight pointlight) {
	return pointlight.color * calculateDiffuse(facenormal, worldspaceposition, pointlight.position);
}

vec3 calculateSpecular(vec3 worldspaceposition, vec3 facenormal, vec3 lightposition, vec4 specularcolor) {
	float specularity = specularcolor.a;
	
	vec3 tolightdir = normalize(lightposition - worldspaceposition);
	vec3 viewdir = normalize(campos - worldspaceposition);
	vec3 halfwaydir = normalize(tolightdir + viewdir);
	float specularhighlight = pow(max(dot(facenormal, halfwaydir), 0.0), SpecularExponent);
	specularcolor.rgb *= specularhighlight * specularity;
	
	return specularcolor.rgb;
}

float calulateLuminosity(vec3 worldspaceposition, PointLight light) {
	float distancetolight = length(light.position - worldspaceposition);
	float attenuation = 1 + light.linearAttenuation * distancetolight + light.quadraticAttenuation * (distancetolight * distancetolight);
	float luminosity = 1.0 / attenuation;
	
	return luminosity;
}
float calulateLuminosity(vec3 worldspaceposition, SpotLight light) {
	float distancetolight = length(light.position - worldspaceposition);
	float attenuation = 1 + light.linearAttenuation * distancetolight + light.quadraticAttenuation * (distancetolight * distancetolight);
	float luminosity = 1.0 / attenuation;
	luminosity *= 1 + ((light.outerCone + 1.0) / 2.0);
	
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
	vec4 vertposition = texture(PositionTexture, textureCoord);
	vertposition.xy = (textureCoord * 2.0 - 1.0);
	vertposition.w = 1.0;
	vec4 cameraspaceposition = InverseVPMatrix * vertposition;
	vec3 worldspaceposition = cameraspaceposition.xyz / cameraspaceposition.w;
	
	return worldspaceposition;
}

vec3 calcWorldPosition(float depth, vec3 view_ray, vec3 cam_position)
{
	view_ray = normalize(view_ray);
	return view_ray * depth - cam_position;
}

void main(void) {
	vec4 albedocolor = texture(albedoTexture, textureCoord);
	if (albedocolor.a < 0.1)
		discard;
	
	vec4 cameraspaceposition = texture(PositionTexture, textureCoord);
	vec3 worldspaceposition = cameraspaceposition.xyz + campos;
	//vec3 worldspaceposition = calcWorldPosition(cameraspaceposition.a, viewRay, campos);
	
	vec3 facenormal = texture(normalTexture, textureCoord).rgb;
	
	vec4 specularmap = texture(specularTexture, textureCoord);
	
	// Ambiance
	vec3 ambientcolor = calulateAmbience(albedocolor);
	
	pixelColor = ambientcolor;
	
	// Point Lights
	for (int i=0; i<numberOfPointLights; i++) {
		PointLight light = pointLights[i];
		
		// Diffuse
		vec3 diffusecolor = calculateDiffuse(facenormal, worldspaceposition, light) * albedocolor.rgb;
		
		// Specular (Blinn-Phong)
		vec3 specularcolor = calculateSpecular(worldspaceposition, facenormal, light.position, specularmap);
		
		// Attenuation
		float luminosity = calulateLuminosity(worldspaceposition, light);
		
		diffusecolor *= luminosity;
		specularcolor *= luminosity;
		pixelColor += (ambientcolor * luminosity) + diffusecolor + specularcolor;
	}
	
	// Directional lights
	for (int i=0; i<numberOfDirectionalLights; i++) {
		DirectionalLight directionallight = directionalLights[i];
		vec3 diffusecolor = calculateDiffuse(facenormal, directionallight) * albedocolor.rgb;
		pixelColor += diffusecolor;
	}
	
	// Spot lights
	for (int i=0; i<numberOfSpotLights; i++) {
		SpotLight light = spotLights[i];
		
		// Diffuse
		vec3 diffusecolor = calculateDiffuse(worldspaceposition, facenormal, light) * albedocolor.rgb;
		
		// Specular (Blinn-Phong)
		vec3 specularcolor = calculateSpecular(worldspaceposition, facenormal, light.position, specularmap);
		
		// Attenuation
		float luminosity = calulateLuminosity(worldspaceposition, light);
		
		diffusecolor *= luminosity;
		specularcolor *= luminosity;
		
		pixelColor += (ambientcolor * luminosity) + diffusecolor + specularcolor;
	}
}