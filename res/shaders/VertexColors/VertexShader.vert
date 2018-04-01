#version 330 core

in vec3 Position;
in vec3 Color;

out vec3 BlendedColor;

void main(void) {
	gl_Position = vec4(Position, 1.0);
	BlendedColor = Color;
}