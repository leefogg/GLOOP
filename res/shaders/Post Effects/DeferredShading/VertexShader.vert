#version 400 core

in vec3 Position;
in vec2 TextureCoords;

uniform vec3 campos;
uniform mat4 ModelMatrix;

out vec2 textureCoord;
out vec3 viewRay;

void main(void) {
	gl_Position = ModelMatrix * vec4(Position, 1.0);
	textureCoord = TextureCoords;
	
	viewRay = gl_Position.xyz + campos;
}