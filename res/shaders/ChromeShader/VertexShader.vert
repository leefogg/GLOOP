#version 150

in vec3 VertexPosition;
in vec2 TextureCoords;
in vec3 VertexNormal;
in vec3 VertexTangent;

uniform mat4
	ModelMatrix,
	VPMatrix;
uniform vec3 CameraPos;

out vec3 FragLocalPos;
out vec3 SurfaceNormal;
out mat3 TBNMatrix;
out vec2 SurfaceTexCoord;

void main(void) {
	gl_Position = VPMatrix * ModelMatrix * vec4(VertexPosition, 1.0) ;
	
	FragLocalPos = (ModelMatrix * vec4(VertexPosition, 1.0)).xyz - CameraPos;
	SurfaceNormal = VertexNormal;
	SurfaceTexCoord = TextureCoords;
	
	vec3 facenormal = normalize((mat3(ModelMatrix) * VertexNormal));
	vec3 tangent = normalize((mat3(ModelMatrix) * VertexTangent)); // Not sure why this becomes unnormalized
	//tangent = tangent - dot(tangent, facenormal) * facenormal; // Realign Tangent using normal
	vec3 bitangent = normalize(cross(tangent, facenormal)); // Calculate bi-tangent using tangent
	TBNMatrix = mat3(tangent, bitangent, facenormal);
}