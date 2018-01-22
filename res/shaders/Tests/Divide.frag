#version 330 core

in vec2 fragCoord;

uniform vec2 Resolution;
uniform float Time;

out vec4 fragColor;

const float lineScale = 40.0;

float GetColor(vec2 pos) {
    float radius = atan(pos.x, pos.y);
	float num = abs(pos.x) + abs(pos.y);
    //float num = length(pos);
    float curLine = floor(num * lineScale);
    float speed = sin(curLine * 8957475.);
    float offset = fract(sin(speed));
    
    float c = step(.4, tan(radius + offset + speed * Time ));
    
    float rnd = offset;
    return c * rnd;
}

void main(void) {
	vec2 uv = (fragCoord - 0.5 * Resolution.xy) / Resolution.y;
    
	fragColor = vec4(GetColor(uv), GetColor(uv - 0.003), GetColor(uv), 1.0);
}

