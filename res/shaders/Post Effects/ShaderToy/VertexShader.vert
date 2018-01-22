 #version 400 core

in vec3 Position;
in vec2 TextureCoords;

out vec2 fragCoord;

uniform mat4 ModelMatrix;
uniform vec2 Resolution;

void main(void) {
	gl_Position = ModelMatrix * vec4(Position, 1.0);
	fragCoord = TextureCoords * Resolution;
}