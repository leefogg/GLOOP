vec3 calculateSpecular(
	vec3 worldspaceposition, 
	vec3 facenormal, 
	vec3 lightposition,
	vec3 lightcolor, 
	float specularity, 
	float exponent, 
	float quadraticattenuation
	) {
	vec3 lightdir = worldspaceposition - lightposition;
	vec3 viewdir = campos - worldspaceposition;
	float distance = length(lightdir) + length(viewdir);
	float luminosity = calulateLuminosity(distance, quadraticattenuation);
	
	lightdir = normalize(lightdir);
	viewdir = normalize(viewdir);
	vec3 reflectdir = reflect(lightdir, facenormal);
	
	float specularcontribution = max(dot(viewdir, reflectdir), 0.0);
	float spec = pow(specularcontribution, max(exponent, 1.0));
	return spec * specularity * luminosity * lightcolor;
}