#version 150

in vec3 Position;
in vec2 TextureCoords;

out vec2 blurTextureCoords[11];
out vec2 texCoord;

uniform mat4 ModelMatrix;
uniform int screenHeight;

void main(void) {
	gl_Position = ModelMatrix * vec4(Position, 1.0);

	texCoord = TextureCoords;
}
