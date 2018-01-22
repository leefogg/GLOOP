#version 400 core

in vec2 textureCoord;

out vec3 outColor;

uniform sampler2D Texture1, Texture2;

void main(void) {
	vec3 color1 = texture(Texture1, textureCoord).rgb;
	vec3 color2 = texture(Texture2, textureCoord).rgb;
	outColor = color1 + color2;
}