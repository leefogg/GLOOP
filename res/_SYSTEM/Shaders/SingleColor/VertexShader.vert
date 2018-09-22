#version 150

in vec3 Position;

uniform mat4
	VPMatrix,
	ModelMatrix;

void main(void) {
	gl_Position = VPMatrix * ModelMatrix * vec4(Position, 1);
}