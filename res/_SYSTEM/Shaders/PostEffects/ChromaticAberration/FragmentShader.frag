#version 330

uniform float Offset = 0.02;
uniform float Rotation = 0;
uniform sampler2D Texture;
uniform vec2 Resolution;

in vec2 textureCoord;

out vec3 outColor;

void main(void) {
	vec2 uv = textureCoord;
	
    vec2 offset = vec2(cos(Rotation) * Offset, sin(Rotation) * Offset) / Resolution;
    outColor.r = texture(Texture, uv+offset).r;
    outColor.g = texture(Texture, uv).g;
    outColor.b = texture(Texture, uv-offset).b;
}