#version 400 core

out vec4 out_Color;

uniform float znear, zfar;

void main(void) {
	float depth = gl_FragCoord.z / gl_FragCoord.w;
	depth += znear;
	depth /= zfar - znear;
	
	out_Color = vec4(depth, depth, depth, 1);
}