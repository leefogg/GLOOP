#version 400 core

in vec2 FaceTextureCoord;

out vec4 out_Color;

uniform sampler2D Texture;

uniform float gamma = 0.75;

void main(void) {
	vec3 pixelcolor = texture(Texture, FaceTextureCoord).rgb;
	
	out_Color.rgb = 1 - exp(-pixelcolor.rgb * gamma);
}