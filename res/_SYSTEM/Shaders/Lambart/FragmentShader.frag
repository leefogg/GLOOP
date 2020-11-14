#version 150

in vec2 textureCoord;
in vec3 fragLocation;
in vec3 FaceNormal;

uniform sampler2D Texture;
uniform vec3 TextureTint = vec3(1.0, 1.0, 1.0);
uniform vec3 LightPosition;
uniform vec3 ambientLight = vec3(0.5);
uniform vec3 LightColor = vec3(1.0,1.0,1.0);
uniform float 
	LightQuadraticAttenuation = 0.032,
	LightBrightness = 1.0;

out vec4 outColor;

void main(void) {
	vec4 texcolor = texture(Texture, textureCoord) * vec4(TextureTint, 1.0);
	if (texcolor.a < 1.0/255.0)
		discard;
	
	// Diffuse
	vec3 tolightdir = LightPosition - fragLocation;
	float distance = length(tolightdir);
	tolightdir =  normalize(tolightdir);
	float directiondiff = max(dot(FaceNormal, tolightdir), 0.0);
	vec3 diffusecolor = texcolor.rgb * (LightColor * directiondiff);
	// Attenuation
	float attenuation = 1.0 + LightQuadraticAttenuation * (distance * distance);
	float luminosity = 1.0 / attenuation;
	diffusecolor *= luminosity;
	
	// Ambient
	vec3 ambientcolor = texcolor.rgb * ambientLight;
	ambientcolor *= luminosity;
	
	
	outColor = vec4(ambientcolor + diffusecolor, texcolor.a);
}