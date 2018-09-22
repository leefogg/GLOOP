#version 150

in vec2 FaceTextureCoord;

out vec4 out_Color;

uniform sampler2D textureMap;
in vec3 FaceNormal;

void main(void) {
	vec3 norm = normalize(FaceNormal);
	norm = 0.5 + norm;
	norm /= 2;
	out_Color = vec4(norm, 1);
}