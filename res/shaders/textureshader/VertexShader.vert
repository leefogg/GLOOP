#version 150

in vec3 Position;
in vec2 TextureCoords;

uniform mat4
	VPMatrix,
	ModelMatrix;

out vec2 FaceTextureCoord;

void main(void) {
	gl_Position = VPMatrix * ModelMatrix * vec4(Position, 1.0);
	FaceTextureCoord = TextureCoords;
}