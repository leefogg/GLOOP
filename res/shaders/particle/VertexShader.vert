#version 330 core

layout (location = 0) in vec3 Position;
layout (location = 1) in vec2 TextureCoords;
layout (location = 2) in vec3 ParticlePosition;

uniform mat4 ProjectionMatrix;
uniform mat4 ViewMatrix;

out vec2 textureCoord;

void main(void) {
	// Create identity matrix and apply particle position translation
	mat4 modelmatrix;
	modelmatrix[0].xyz = vec3(1,0,0);
	modelmatrix[1].xyz = vec3(0,1,0);
	modelmatrix[2].xyz = vec3(0,0,1);
	modelmatrix[3][3] = 1;
	modelmatrix[3].xyz = ParticlePosition;

	mat4 modelviewmatrix = ViewMatrix * modelmatrix;
	// Remove rotation part after applying other viewmatrix attributes
	modelviewmatrix[0] = vec4(1,0,0,0);
	modelviewmatrix[1] = vec4(0,1,0,0);
	modelviewmatrix[2] = vec4(0,0,1,0);
	modelviewmatrix[3][3] = 1;
	
	gl_Position = ProjectionMatrix * modelviewmatrix * vec4(Position , 1.0);
	textureCoord = TextureCoords;
}