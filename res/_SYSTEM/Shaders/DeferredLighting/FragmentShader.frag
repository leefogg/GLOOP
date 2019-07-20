#version 330 core
layout(early_fragment_tests) in;

in vec2 uv;
in vec3 FaceNormal, fragWorldPos, fragLocalPos;
in mat3 TBNMatrix;
in vec4 vertPosition;

uniform bool 
	HasAlbedoMap = false,
	HasNormalMap = false,
	HasSpecularMap = false,
	HasEnvironmentMap = false,
	HasDepthMap = false;
uniform sampler2D 
	albedoMap,
	normalMap,
	specularMap,
	depthMap;
uniform samplerCube environmentMap;
uniform vec3 envMapPos, envMapSize;
uniform float Specularity, Roughness;
uniform vec4 AlbedoColor = vec4(1.0, 0.0, 0.0, 1.0);
uniform float 
	reflectivity = 0.0,
	refractivity = 0.0,
	normalMapScale = 1.0;
uniform vec3 campos;
uniform float znear, zfar;
uniform mat4 InverseVPMatrix;
uniform float heightScale = 0.1;
uniform float 
	FresnelBias = 0.0,
	FresnelScale = 0.5, 
	FresnelPower = 2.0;
uniform float Time;
	
uniform vec4 refractionIndices = vec4(1.0/1.2);

vec3 BoxMin = envMapPos - envMapSize/2.0;
vec3 BoxMax = envMapPos + envMapSize/2.0;

layout(location=0) out vec4 albedobuffer;
layout(location=1) out vec3 specularbuffer;
layout(location=2) out vec3 normalbuffer;
layout(location=3) out vec4 positionbuffer;

vec2 calcParallaxMapping(sampler2D parallax_texture, vec2 tex_coords, mat3 TBN, vec3 camera_position, vec3 world_position, inout vec3 viewdir) { 
	mat3 tTBN = transpose(TBN);

	vec3 t_camPosition = tTBN * camera_position;
	vec3 t_worldPosition = tTBN * world_position;

	vec3 viewDir = -normalize(t_camPosition - t_worldPosition);

	// number of depth layers
	const float minLayers = 2.0;
	const float maxLayers = 24.0;
	// More layers the more perpendicular view
	float numLayers = mix(maxLayers, minLayers, abs(dot(vec3(0.0, 0.0, 1.0), viewDir)));  
	// calculate the size of each layer
	float layerDepth = 1.0 / numLayers;
	// the amount to shift the texture coordinates per layer (from vector P)
	vec2 P = viewDir.xy / viewDir.z * heightScale; 

	// get initial values
	vec2  currentTexCoords     = tex_coords;
	float currentDepthMapValue = texture(parallax_texture, currentTexCoords).r;

	vec2 deltaTexCoords = P / numLayers;
	float currentLayerDepth = 0.0;
	while(currentLayerDepth < currentDepthMapValue)	{
		// shift texture coordinates along direction of P
		currentTexCoords -= deltaTexCoords;
		// get depthmap value at current texture coordinates
		currentDepthMapValue = texture(parallax_texture, currentTexCoords).r;
		// get depth of next layer
		currentLayerDepth += layerDepth;  
	}

	// -- parallax occlusion mapping interpolation from here on
	// get texture coordinates before collision (reverse operations)
	vec2 prevTexCoords = currentTexCoords + deltaTexCoords;

	// get depth after and before collision for linear interpolation
	float afterDepth  = currentDepthMapValue - currentLayerDepth;
	float beforeDepth = texture(parallax_texture, prevTexCoords).r - currentLayerDepth + layerDepth;

	// interpolation of texture coordinates
	float weight = afterDepth / (afterDepth - beforeDepth);
	vec2 finalTexCoords = mix(currentTexCoords, prevTexCoords, weight);
	
	viewdir += normalize(viewdir) * currentLayerDepth;
	
	return finalTexCoords;
} 

#define MOD3 vec3(443.8975,397.2973, 491.1871)
float rand(vec2 p) {
	vec3 p3  = fract(vec3(p.xyx) * MOD3);
    p3 += dot(p3, p3.yzx + 19.19);
    return fract((p3.x + p3.y) * p3.z);
}

vec3 AABBIntersect(vec3 pos, vec3 dir, vec3 boxmin, vec3 boxmax) {
	// Following is the parallax-correction code
	// Find the ray intersection with box plane
	vec3 firstplaneintersect = (boxmax - pos) / dir;
	vec3 secondplaneintersect = (boxmin - pos) / dir;
	// Get the furthest of these intersections along the ray
	// (Ok because x/0 give +inf and -x/0 give –inf )
	vec3 furthestplane = max(firstplaneintersect, secondplaneintersect);
	// Find the closest far intersection
	float dist = min(min(furthestplane.x, furthestplane.y), furthestplane.z);

	// Get the intersection position
	return pos + dir * dist;
}

vec3 parallaxCorrectedRefraction(vec3 fragpos, vec3 normal, vec3 campos, float refractionindex, vec3 boxpos, vec3 boxmin, vec3 boxmax) {
	vec3 tofragdir = fragpos - campos;
	vec3 reflectiondir = refract(tofragdir, normal, refractionindex);
	vec3 intersectpos = AABBIntersect(fragpos, reflectiondir, boxmin, boxmax);
	
	return intersectpos - boxpos;
}

vec3 parallaxCorrectedEnvMap(vec3 fragpos, vec3 dir, vec3 boxpos, vec3 boxmin, vec3 boxmax, samplerCube envmap) {
	vec3 intersectpos = AABBIntersect(fragpos, dir, boxmin, boxmax);
	
	dir = intersectpos - boxpos;

	return texture(envmap, dir).rgb;
}


void main(void) {
	vec3 fraglocalpos = fragLocalPos;
	
	vec2 texcoords = uv;
	#if defined PARALLAXMAPPING
	if (HasDepthMap)
		texcoords = calcParallaxMapping(depthMap, uv, TBNMatrix, campos, fragWorldPos, fraglocalpos);
	#endif
	
	
	vec4 albedocolor;
	if (HasAlbedoMap) {
		albedocolor = texture(albedoMap, texcoords);
	} else {
		albedocolor = AlbedoColor;
	}
	if (albedocolor.a < 1.0/255.0)
		discard;
	albedobuffer.rgb = albedocolor.rgb;
	
	// Calculate normal
	vec3 norm;
	#if defined NORMALMAPPING
	if (HasNormalMap) {
		norm = texture(normalMap, texcoords).rgb;
		norm = norm * 2.0 - 1.0; // To tangent space ([0,1] to [-1,1])
		norm = mix(vec3(0.0,0.0,1.0), norm, normalMapScale);
	} else {
		norm = vec3(0.0, 0.0, 1.0); // Straight up
	}
	#else
		norm = vec3(0.0, 0.0, 1.0); // Straight up
	#endif
	// Transform here and work with, then save last
	norm = TBNMatrix * norm; // To world space
	norm = normalize(norm);
	
	// Calculate Fresnel effect
	float fresnalbias = 0;
	#if defined FRESNEL
	fresnalbias = FresnelBias + FresnelScale * pow(dot(normalize(fragLocalPos), -norm), FresnelScale);
	#endif
	
	vec3 reflectioncolor;
	vec3 refractioncolor;
	#if defined ENVIRONMENTMAP
	if (HasEnvironmentMap) {
		#if defined REFLECTIVITY
		bool isreflective = reflectivity > 1.0/256.0;
		if (isreflective) {
			vec3 tofragdir = fragWorldPos - campos;
			vec3 reflectiondir = reflect(tofragdir, norm);
			reflectioncolor = parallaxCorrectedEnvMap(
				fragWorldPos,
				reflectiondir,
				envMapPos,
				BoxMin,
				BoxMax,
				environmentMap
			);
		}
		#endif

		#if defined REFRACTIVITY
		bool isrefractive = refractivity > 1.0/256.0;		
		if (isrefractive) {
			#if defined CHROMATICABERRATION
			vec3 refractreddir = parallaxCorrectedRefraction(fragWorldPos, norm, campos, refractionIndices.r, envMapPos, BoxMin, BoxMax);
			vec3 refractgreendir = parallaxCorrectedRefraction(fragWorldPos, norm, campos, refractionIndices.g, envMapPos, BoxMin, BoxMax);
			vec3 refractbluedir = parallaxCorrectedRefraction(fragWorldPos, norm, campos, refractionIndices.b, envMapPos, BoxMin, BoxMax);

			refractioncolor = vec3(
				texture(environmentMap, refractreddir).r,
				texture(environmentMap, refractgreendir).g,
				texture(environmentMap, refractbluedir).b
			);
			#else
			vec3 refractiondir = parallaxCorrectedRefraction(fragWorldPos, norm, campos, refractionIndices.a, envMapPos, BoxMin, BoxMax);
			refractioncolor = texture(environmentMap, refractiondir).rgb;
			#endif
			
		}
		#endif
	}
	#endif
	
	
	#if defined FRESNEL
	// Triangular mix :D
	vec3 reflectionrefractioncolor = 
		mix(
			mix(refractioncolor, reflectioncolor, reflectivity),
			mix(reflectioncolor, refractioncolor, refractivity),
			max(0.0,fresnalbias)
		);
	#else
	vec3 reflectionrefractioncolor = reflectioncolor * reflectivity + refractioncolor * refractivity;
	#endif
	albedobuffer.rgb = mix(albedobuffer.rgb, reflectionrefractioncolor, min(1.0, reflectivity + refractivity));
	albedobuffer.a = albedocolor.a;
	
	
	float specularity = 1.0, roughness = 1.0;
	#if defined SPECULARMAPPING
	if (HasSpecularMap) {
		vec3 spectexture = texture(specularMap, texcoords).rgb;
		specularity = spectexture.r;
	}
	#endif
	specularity *= Specularity;
	roughness *= Roughness;
	specularbuffer = vec3(specularity, roughness, 1.0);
	
	
	// Position buffer
	float depth = length(fraglocalpos) / zfar;
	positionbuffer = vec4(fraglocalpos, depth);
	
	
	// Finally save
	norm = 0.5 + norm / 2.0;
	normalbuffer = norm;
}