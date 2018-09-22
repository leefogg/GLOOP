#version 150

in vec3 Position;

uniform mat4
	ProjectionMatrix,
	ViewMatrix,
	ModelMatrix;

void main(void) {
	gl_Position = ProjectionMatrix * ViewMatrix * ModelMatrix * vec4(Position, 1.0);
}