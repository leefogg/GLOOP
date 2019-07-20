#version 150

in vec4 worldSpacePosition;

uniform float znear = 0.01, zfar = 1000;
uniform vec3 campos;

out vec4 out_Color;


void main(void) {
	float depth = length(worldSpacePosition.xyz - campos);
	//float depth = gl_FragCoord.z / gl_FragCoord.w;
	depth += znear;
	depth /= zfar - znear;
	
	out_Color = vec4(depth, depth, depth, 1);
}