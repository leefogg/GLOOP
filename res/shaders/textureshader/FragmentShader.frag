#version 400 core

in vec2 FaceTextureCoord;

out vec4 out_Color;

uniform sampler2D Texture;

void main(void) {
	out_Color = texture(Texture, FaceTextureCoord);
}