#version 150

in vec3 Position;

uniform mat4
	VPMatrix,
	ModelMatrix;
	
out vec4 worldSpacePosition;

void main(void) {
	worldSpacePosition = ModelMatrix * vec4(Position, 1.0);
	gl_Position = VPMatrix * worldSpacePosition;
}