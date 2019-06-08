#version 330

//TODO: Move these to an include file
in vec2 textureCoord;

uniform sampler2D 
	positionTexture,
	normalTexture,
	specularTexture;

uniform float znear, zfar;
uniform vec3 campos;

layout(location=4) out vec3 pixelColor;

void main(void) {
	vec3 localpos = texture(positionTexture, textureCoord).rgb;
	float depth = (length(localpos) - znear) / zfar;
	pixelColor = vec3(depth);
}