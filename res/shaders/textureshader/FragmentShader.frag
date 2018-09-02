#version 150

in vec2 FaceTextureCoord;

out vec4 out_Color;

uniform sampler2D Texture;

void main(void) {
	out_Color = texture(Texture, FaceTextureCoord);
	if (out_Color.a < 1.0/255.0)
		discard;
}
