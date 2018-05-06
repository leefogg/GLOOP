#version 150

in vec2 FaceTextureCoord;

out vec4 out_Color;

uniform sampler2D Texture;

uniform float gamma = 2.2;

void main(void) {
	vec3 pixelcolor = texture(Texture, FaceTextureCoord).rgb;
	
	out_Color.rgb = pow(pixelcolor, vec3(1/gamma));
}