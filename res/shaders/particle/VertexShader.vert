#version 330 core

in vec3 Position;
in vec2 TextureCoords;

uniform mat4
	ProjectionMatrix,
	ModelViewMatrix;

out vec2 textureCoord;

void main(void) {
	
	gl_Position = ProjectionMatrix * ModelViewMatrix * vec4(Position, 1.0);
	textureCoord = TextureCoords;
}