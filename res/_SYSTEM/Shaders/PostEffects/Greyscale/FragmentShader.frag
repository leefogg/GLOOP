#version 150

in vec2 FaceTextureCoord;

out vec4 out_Color;

uniform sampler2D Texture;

const vec3 lumabias = vec3(0.299, 0.587, 0.114);

void main(void) {
	vec3 pixelcolor = texture(Texture, FaceTextureCoord).rgb;
	
	// Convert pixel color to brightness perceived by human eye
	out_Color.rgb = vec3(
		pixelcolor.r * lumabias.r + 
		pixelcolor.g * lumabias.g +
		pixelcolor.b * lumabias.b
	);
	out_Color.rgb /= 3.0;
}