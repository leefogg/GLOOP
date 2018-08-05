#version 150

in vec3 Position;
in vec2 TextureCoords;

// Character information
uniform vec2 scale, offset;

uniform mat4 ModelMatrix;

out vec2 textureCoord;

void main(void) {
	gl_Position = ModelMatrix * vec4(Position, 1.0);
	textureCoord = (TextureCoords * vec2(1.0, -1.0) + vec2(0,1.0)) * scale + offset;
}