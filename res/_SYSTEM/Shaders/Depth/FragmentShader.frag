#version 150

in vec4 worldSpacePosition;
in vec2 FaceTextureCoord;

uniform float znear = 0.01, zfar = 1000;
uniform vec3 campos;
#if defined HasTexture
uniform sampler2D albedoMap;
#endif

out vec4 out_Color;


void main(void) {
	out_Color = vec4(0.0, 0.0, 0.0, 1.0);
	
	#if defined HasTexture
	vec4 textureColor = texture(albedoMap, FaceTextureCoord);
	if (textureColor.a < 1.0/255.0)
		discard;
	#endif

	float depth = length(worldSpacePosition.xyz - campos);
	//float depth = gl_FragCoord.z / gl_FragCoord.w;
	depth += znear;
	depth /= zfar - znear;
	
	out_Color = vec4(depth, depth, depth, 1.0);
}