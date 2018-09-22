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
	vec4 vertexworldposition = ModelMatrix * vec4(Position, 1.0);
	
	gl_Position = VPMatrix * vertexworldposition;
	
	// Will be averaged by the shader pipeline
	fragLocation = vertexworldposition.xyz; 
	textureCoord = TextureCoords;
	FaceNormal = normalize((ModelMatrix * vec4(VertexNormal, 0.0)).xyz);
}