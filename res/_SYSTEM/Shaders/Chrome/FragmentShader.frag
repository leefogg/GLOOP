#version 150

in vec3 FragLocalPos;
in vec3 SurfaceNormal;
in mat3 TBNMatrix;

uniform samplerCube environmentMap;
uniform vec3 envMapPos, envMapSize;
uniform vec3 cameraPos;

out vec3 fragColor;

vec3 BoxMin = envMapPos - envMapSize/2.0;
vec3 BoxMax = envMapPos + envMapSize/2.0;
	
vec3 parallaxCorrectedEnvMap(vec3 fragpos, vec3 camerapos, vec3 normal ,vec3 boxpos, vec3 boxmin, vec3 boxmax, samplerCube envmap) {
	vec3 tofragdir = fragpos - camerapos;
	vec3 reflectiondir = reflect(tofragdir, normal);

	// Following is the parallax-correction code
	// Find the ray intersection with box plane
	vec3 firstplaneintersect = (boxmax - fragpos) / reflectiondir;
	vec3 secondplaneintersect = (boxmin - fragpos) / reflectiondir;
	// Get the furthest of these intersections along the ray
	// (Ok because x/0 give +inf and -x/0 give –inf )
	vec3 furthestplane = max(firstplaneintersect, secondplaneintersect);
	// Find the closest far intersection
	float dist = min(min(furthestplane.x, furthestplane.y), furthestplane.z);

	// Get the intersection position
	vec3 intersectfragpos = fragpos + reflectiondir * dist;
	// Get corrected reflection
	reflectiondir = intersectfragpos - boxpos;

	return texture(envmap, reflectiondir).rgb;
}

void main(void) {
	vec3 normal = vec3(0.0, 0.0, 1.0);
	normal = TBNMatrix * normal;
	normal = normalize(normal);

	vec3 reflection = parallaxCorrectedEnvMap(
		FragLocalPos + cameraPos,
		cameraPos,
		SurfaceNormal,
		envMapPos,
		BoxMin,
		BoxMax, 
		environmentMap
	);
	
	fragColor = reflection;
}