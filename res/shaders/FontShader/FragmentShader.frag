#version 150

in vec2 textureCoord;

out vec4 fragColor;

uniform sampler2D TextureAtlas;

void main(void) {
	vec4 color = texture(TextureAtlas, textureCoord);
	if (color.a < 0.9)
		discard;
	
	fragColor = color;
}