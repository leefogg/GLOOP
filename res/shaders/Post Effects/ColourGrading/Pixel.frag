#version 150

uniform int precision = 32;
uniform int LUTWidth = 1024;
uniform Sampler2D frame;
uniform Sampler2D lut;

in vec2 uv;
out vec3 pixelColor;


void main() {
	float divisionsize = 1.0 / precision;
	vec2 pixelsize = vec2(
		divisionsize / precision,
		divisionsize
	);
	
	vec3 currentcolor = texture(frame, uv).rgb;
	// Scale down and snap to LUT's precision
	float dividend = LUTWidth/precision;
	currentcolor.rg /= dividend;
	currentcolor.rg -= currentcolor.rg % (1.0 / dividend);
	
	// Find corresponding pixel color in LUT
	currentcolor.b *= precision;
	currentcolor.b -= currentcolor.b % precision;
	vec2 uv = vec2(
		(divisionsize * currentcolor.b) + pixelsize.x * currentcolor.r,
		pixelsize.y * currentcolor.g
	);
	
	pixelColor = texture(lut, uv);
}