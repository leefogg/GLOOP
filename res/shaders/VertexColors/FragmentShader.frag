#version 330 core

in vec3 BlendedColor;

out vec3 PixelColor;

void main(void) {
	PixelColor = BlendedColor;
}