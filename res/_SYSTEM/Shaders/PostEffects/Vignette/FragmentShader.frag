#version 150

in vec2 fragCoord;
out vec4 fragColor;

uniform sampler2D Texture;
uniform float 
	Start = 0.0, 
	End = 0.75;
uniform vec2 Resolution;

const vec2 center = vec2(0.5);

void main(void) {
	vec2 uv = fragCoord/Resolution.xy;
	
    float dist = 1.0 - distance(center, uv);
    dist = smoothstep(Start, End, dist);
	
	fragColor = texture(Texture, uv) * dist;
}