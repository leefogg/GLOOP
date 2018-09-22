#version 150

in vec3 Position;

out vec3 TextureCoords;

uniform mat4
	ProjectionMatrix,
	ViewMatrix;

void main(void) {
	mat4 rotationmatrix = ViewMatrix;
	// Remove translation
	rotationmatrix[3][0] = 0;
	rotationmatrix[3][1] = 0;
	rotationmatrix[3][2] = 0;
	vec4 pos = ProjectionMatrix * rotationmatrix * vec4(Position, 1.0);
	gl_Position = pos.xyww; // Ensure z = w so depth = 1
	
	TextureCoords = Position;
}