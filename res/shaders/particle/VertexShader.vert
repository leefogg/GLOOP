#version 400 core

in vec3 Position;
in vec2 TextureCoords;

uniform mat4
	ProjectionMatrix,
	ViewMatrix,
	ModelMatrix;

out vec2 textureCoord;

void main(void) {
	mat4 mvpmatrix = ViewMatrix * ModelMatrix;
	// Wipe rotation to make it always look at camera
	mvpmatrix[0].xyz = vec3(1,0,0);
	mvpmatrix[1].xyz = vec3(0,1,0);
	mvpmatrix[2].xyz = vec3(0,0,1);
	gl_Position = ProjectionMatrix * mvpmatrix * vec4(Position, 1.0);
	textureCoord = TextureCoords;
}