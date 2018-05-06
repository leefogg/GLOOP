#version 150

out vec3 FragColor;

in vec3 TextureCoords;

uniform samplerCube cubeMap;

void main(void) {
	FragColor = texture(cubeMap, TextureCoords).rgb;
}