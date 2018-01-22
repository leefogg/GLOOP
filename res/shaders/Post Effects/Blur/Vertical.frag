#version 150

out vec3 out_colour;

in vec2 blurTextureCoords[11];
in vec2 texCoord;
uniform int screenHeight;

uniform sampler2D Texture;


void main(void) {
	float weights[11];
	weights[0] = 0.000003;
	weights[1] = 0.000229;
	weights[2] = 0.005977;
	weights[3] = 0.060598;
	weights[4] = 0.24173;
	weights[5] = 0.382925;
	weights[6] = 0.24173;
	weights[7] = 0.060598;
	weights[8] = 0.005977;
	weights[9] = 0.000229;
	weights[10] = 0.000003;
	
	float pixelsize = 1.0 / screenHeight;
	out_colour = vec3(0.0);
	for (int i=-5; i<=5; i++) {
		out_colour += texture(Texture, texCoord + vec2(0, pixelsize * i)).rgb * weights[i+5];
	}
}