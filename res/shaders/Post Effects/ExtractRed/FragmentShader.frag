#version 400 core

in vec2 FaceTextureCoord;

out vec4 out_Color;

uniform sampler2D TextureID;

void main(void) {
	float red = texture(TextureID, FaceTextureCoord).r;
	out_Color = vec4(red,0,0,0);
}