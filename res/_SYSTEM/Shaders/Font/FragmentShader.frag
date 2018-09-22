#version 150

in vec2 textureCoord;

out vec4 fragColor;

uniform sampler2D TextureAtlas;
uniform float 
	Thickness = 0.5,
	EdgeWidth = 0.1;
uniform vec3 Color = vec3(1.0);

void main(void) {
	vec4 color = texture(TextureAtlas, textureCoord);
	
	float dist = 1.0 - color.a;
	
	float alpha = 1.0 - smoothstep(Thickness, Thickness+EdgeWidth, dist);
	
	fragColor = vec4(Color, 1.0) * alpha;
}