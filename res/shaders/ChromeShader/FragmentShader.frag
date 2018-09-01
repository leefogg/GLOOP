#version 150

in vec3 FragLocalPos;
in vec3 SurfaceNormal;
in mat3 TBNMatrix;

uniform samplerCube environmentMap;
uniform vec3 camPos;

out vec3 fragColor;

void main(void) {
	vec3 normal = vec3(0.0, 0.0, 1.0);
	normal = TBNMatrix * normal;
	normal = normalize(normal);

	vec3 reflectdirection = reflect(FragLocalPos, normal);
	vec3 reflection = texture(environmentMap, reflectdirection).rgb;
	
	fragColor = reflection;
}