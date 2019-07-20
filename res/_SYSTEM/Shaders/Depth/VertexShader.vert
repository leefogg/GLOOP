#version 150

in vec3 Position;
in vec2 TextureCoords;

uniform mat4
	VPMatrix,
	ModelMatrix;
	
out vec4 worldSpacePosition;
out vec2 FaceTextureCoord;

void main(void) {
	worldSpacePosition = ModelMatrix * vec4(Position, 1.0);
	gl_Position = VPMatrix * worldSpacePosition;
	FaceTextureCoord = TextureCoords;
}