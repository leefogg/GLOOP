#version 400 core

in vec2 FaceTextureCoord;

uniform sampler2D Texture;
uniform float exposure = 0.75;

out vec4 out_Color;

void main(void) {
	vec3 pixelcolor = texture(Texture, FaceTextureCoord).rgb;
	
	out_Color.rgb = 1 - exp(-pixelcolor.rgb * exposure);
}