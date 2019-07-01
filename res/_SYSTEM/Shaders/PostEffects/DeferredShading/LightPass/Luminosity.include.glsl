float calulateLuminosity(float distance, float quadraticattenuation) {
	float attenuation = 1.0 + quadraticattenuation * (distance * distance);
	float luminosity = 1.0 / attenuation;
	
	return luminosity;
}
float calulateLuminosity(vec3 lightpos, vec3 worldpos, float quadraticAttenuation) {
	float distancetolight = length(lightpos - worldpos);
	return calulateLuminosity(distancetolight, quadraticAttenuation);
}