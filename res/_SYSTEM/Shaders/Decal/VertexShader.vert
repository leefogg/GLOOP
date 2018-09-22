#version 330 core

in vec3 Position;

uniform mat4
	VPMatrix,
	ModelMatrix;
	
out vec4 clipspace;

void main(void) {
	clipspace = VPMatrix * ModelMatrix * vec4(Position, 1.0);
	gl_Position = clipspace;
}