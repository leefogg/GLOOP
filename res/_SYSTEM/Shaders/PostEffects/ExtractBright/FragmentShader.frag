#version 150

in vec2 textureCoord;

out vec3 outColor;

uniform sampler2D Texture;

void main(void) {
	outColor = vec3(0.0);
	
	vec3 color = texture(Texture, textureCoord).rgb;
	float brightness = (color.r * 0.2126) + (color.g * 0.7152) + (color.b * 0.0722);

	outColor = color * (max(0, brightness - 0.95) * 20.0);
}