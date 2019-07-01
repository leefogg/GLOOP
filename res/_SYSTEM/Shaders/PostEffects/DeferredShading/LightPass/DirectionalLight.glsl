#version 330

uniform vec3 direction;
uniform vec3 diffuseColor;

#include <GBuffers.include.glsl>

vec3 calculateDiffuse(vec3 facenormal) {
	float directiondiff = max(dot(direction, facenormal), 0.0);
	vec3 diffusecolor = diffuseColor * directiondiff;
	
	return diffusecolor;
}

void main(void) {
	float specularity, roughness, stencil;
	readSpecularMap(textureCoord, specularity, roughness, stencil);
	
	// TODO: Move to stencil buffer
	if (stencil < 0.5) // Skip if stencil says so
		discard;
		
	vec3 normal = getNormal(textureCoord);
		
	pixelColor = vec4(calculateDiffuse(normal), 1.0);
}