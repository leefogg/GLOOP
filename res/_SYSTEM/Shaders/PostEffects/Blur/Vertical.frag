#version 150

uniform sampler2D Texture;
uniform float weight[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);

in vec2 blurTextureCoords[11];
in vec2 texCoord;

out vec3 PixelColor;

void main(void) {
	PixelColor = vec3(0.0);
	
	float pixelsize = 1.0 / textureSize(Texture, 0).y;
	for(int i = 1; i < 5; ++i) {
		PixelColor += texture(Texture, texCoord + vec2(0.0, pixelsize * i)).rgb * weight[i];
		PixelColor += texture(Texture, texCoord - vec2(0.0, pixelsize * i)).rgb * weight[i];
	}
	
	PixelColor *= 1.3;
}