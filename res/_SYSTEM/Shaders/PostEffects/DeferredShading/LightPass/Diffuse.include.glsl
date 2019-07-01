float calculateDiffuse(vec3 facenormal, vec3 worldspaceposition, vec3 position) {
	vec3 tolightdir = normalize(position - worldspaceposition);
	float directiondiff = max(dot(facenormal, tolightdir), 0);
	
	return directiondiff;
}