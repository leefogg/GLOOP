#version 330

in vec3 Position;
in vec2 TextureCoords;
in vec3 VertexNormal;
in vec3 Tangent;

uniform mat4 
	ModelMatrix,
	VPMatrix;
uniform vec3 campos;
uniform float znear, zfar;
uniform vec2 TextureRepeat = vec2(1, 1);
uniform vec2 TextureOffset = vec2(0, 0);

out vec2 uv;
out vec3 FaceNormal, fragWorldPos, fragLocalPos;
out mat3 TBNMatrix;
out vec4 vertPosition;


void main(void) {
	gl_Position = VPMatrix * ModelMatrix * vec4(Position, 1);
	
	// PS1 style vertex inaccuracy
	//gl_Position.xyz = gl_Position.xyz - mod(gl_Position.xyz, 0.01);
	
	vertPosition = gl_Position;
	
	uv = (TextureCoords+TextureOffset)*TextureRepeat;	
	
	// This fragments position in world space
	fragWorldPos = (ModelMatrix * vec4(Position, 1)).xyz;
	
	// This fragments position in world space relative to the camera
	fragLocalPos = fragWorldPos - campos;
	
	vec3 facenormal = normalize((mat3(ModelMatrix) * VertexNormal));
	vec3 tangent = normalize((mat3(ModelMatrix) * Tangent)); // Not sure why this becomes unnormalized
	//tangent = tangent - dot(tangent, facenormal) * facenormal; // Realign Tangent using normal
	vec3 bitangent = normalize(cross(tangent, facenormal)); // Calculate bi-tangent using tangent
	TBNMatrix = mat3(tangent, bitangent, facenormal);
	
	FaceNormal = facenormal;
}