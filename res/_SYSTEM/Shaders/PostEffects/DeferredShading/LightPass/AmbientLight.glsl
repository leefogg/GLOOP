#version 330

uniform vec3 ambientColor;

#include <GBuffers.include.glsl>

void main(void) {
	float specularity, roughness, stencil;
	readSpecularMap(textureCoord, specularity, roughness, stencil);
	
	// TODO: Move to stencil buffer
	if (stencil < 0.5) // Skip if stencil says so
		discard;
		
	pixelColor = vec4(ambientColor, 1.0);
}