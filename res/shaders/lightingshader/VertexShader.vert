#version 150

in vec3 Position;
in vec2 TextureCoords;
in vec3 VertexNormal;

out vec2 textureCoord;
out vec3 fragLocation;
out vec3 FaceNormal;

uniform mat4
	VPMatrix,
	ModelMatrix;

void main(void) {
	textureCoord = TextureCoords;
	
	gl_Position = VPMatrix * ModelMatrix * vec4(Position, 1);
	
	vec3 vertexworldposition = (ModelMatrix * vec4(Position, 1)).xyz;
	fragLocation = vertexworldposition; // Will be averaged by the shader pipeline
	
	FaceNormal = normalize((ModelMatrix * vec4(VertexNormal, 0)).xyz);
}