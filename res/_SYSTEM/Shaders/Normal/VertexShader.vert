#version 150

in vec3 Position;
in vec3 VertexNormal;

uniform mat4 
	ProjectionMatrix,
	ViewMatrix,
	ModelMatrix;

out vec3 FaceNormal;

void main(void) {
	gl_Position = ProjectionMatrix * ViewMatrix * ModelMatrix * vec4(Position, 1.0);
	FaceNormal = (ModelMatrix * vec4(VertexNormal,0)).xyz;
}