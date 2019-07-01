in vec2 textureCoord;

uniform sampler2D 
	positionTexture,
	normalTexture,
	specularTexture;
uniform vec3 campos;
uniform mat4 VPMatrix;

const float MaxSpecularExponent = 256.0;

uniform float time;

layout(location=4) out vec4 pixelColor;

void readSpecularMap(vec2 uv, out float specularity, out float roughness, out float stencil) {
	vec4 specularmap = texture(specularTexture, uv);
	specularity = specularmap.r * 100.0;
	roughness = 1-specularmap.g;
	stencil = specularmap.b;
}

vec3 getNormal(vec2 uv) {
	vec4 normalbuffer = texture(normalTexture, uv);
	vec3 facenormal = normalbuffer.rgb * 2.0 - 1.0;
	facenormal = normalize(facenormal);
	
	return facenormal;
}