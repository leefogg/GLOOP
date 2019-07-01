// Used for dithering
#define MOD3 vec3(443.8975,397.2973, 491.1871)
float rand(vec2 p) {
	vec3 p3  = fract(vec3(p.xyx) * MOD3);
    p3 += dot(p3, p3.yzx + 19.19);
    return fract((p3.x + p3.y) * p3.z);
}
vec3 dither(vec2 texcoord, float time) {
	vec2 seed = texcoord;
	seed += fract(time);

	vec3 rnd = vec3(rand(seed) + rand(seed + 0.59374) - 0.5);

	return rnd/255.0;
}