#version 330 core

in vec2 textureCoord;

out vec4 out_Color;

uniform sampler2D Texture;

void main(void) {
	out_Color = texture(Texture, vec2(textureCoord.x, -textureCoord.y));
	if (out_Color.a < 1.0/255.0)
		discard;
}
