#version 330
#include <GBuffers.include.glsl>

uniform float fogDensity;
uniform vec3 fogColor;

void main(void) {
	float specularity, roughness, stencil;
	readSpecularMap(textureCoord, specularity, roughness, stencil);
	
	// TODO: Move to stencil buffer
	if (stencil < 0.5) // Skip if stencil says so
		discard;
		
	vec4 cameraspaceposition = texture(positionTexture, textureCoord);
	
	float dist = length(cameraspaceposition);
	float fogfactor = 1.0 / exp(dist * fogDensity);
	fogfactor = clamp(fogfactor, 0.0, 1.0);
	
	pixelColor = vec4(fogColor, fogfactor);
}