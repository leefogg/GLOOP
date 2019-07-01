#version 330
#include <GBuffers.include.glsl>
#include <Dither.include.glsl>

void main(void) {
	float specularity, roughness, stencil;
	readSpecularMap(textureCoord, specularity, roughness, stencil);
	
	// TODO: Move to stencil buffer
	if (stencil < 0.5) // Skip if stencil says so
		discard;
	
	pixelColor = vec4(dither(textureCoord, time), 1.0);
}