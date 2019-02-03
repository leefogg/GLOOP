#version 150

in vec3 VertexPosition;
in vec2 TextureCoords;
in vec3 VertexNormal;
in vec3 VertexTangent;

uniform mat4
	ModelMatrix,
	VPMatrix;
uniform vec3 cameraPos;

out vec3 FragLocalPos;
out vec3 SurfaceNormal;
out vec2 SurfaceTexCoord;

void main(void) {
	gl_Position = VPMatrix * ModelMatrix * vec4(VertexPosition, 1.0) ;
	
	FragLocalPos = (ModelMatrix * vec4(VertexPosition, 1.0)).xyz - cameraPos;
	SurfaceNormal = normalize((ModelMatrix * vec4(VertexNormal, 0.0)).xyz);
	SurfaceTexCoord = TextureCoords;
}