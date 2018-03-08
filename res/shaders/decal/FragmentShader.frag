#version 330 core

in vec4 clipspace;

uniform mat4 inverseModelMatrix, rotationMatrix;
uniform sampler2D albedomap, specularmap, positionBuffer;
uniform vec3 campos;

layout(location=0) out vec3 albedobuffer;
layout(location=1) out vec3 specularbuffer;
//layout(location=2) out vec3 normalbuffer;

void clip(vec3 space) {
	if (space.x > .5)
		discard;
	if (space.y > .5)
		discard;
	if (space.z > .5)
		discard;
	if (space.x < -.5)
		discard;
	if (space.y < -.5)
		discard;
	if (space.z < -.5)
		discard;
}

void main(void) {
	vec2 ndc = (clipspace.xy / clipspace.w) / 2.0 + 0.5;
	
	vec3 worldspacepos = texture(positionBuffer, ndc).xyz;
	worldspacepos += campos;
	vec3 localpos = (inverseModelMatrix * vec4(worldspacepos, 1.0)).xyz;
	
	/*
	albedobuffer = worldspacepos;
	specularbuffer = localpos;
	*/
	
	clip(localpos);
	
	vec2 uv = (localpos.xz + 0.5);
	vec4 texel = texture(albedomap, uv);
	if (texel.a < 0.1)
		discard;
	
	albedobuffer = texel.rgb;
	specularbuffer = vec3(texture(specularmap, uv).rg, 1.0);
	/*
	normalbuffer = texture(normalmap, uv).rgb;
	normalbuffer = normalize(normalbuffer);
	normalbuffer = (rotationMatrix * vec4(normalbuffer, 1.0)).xyz;
	*/
}