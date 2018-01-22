#version 400 core

in vec2 textureCoord;
in vec3 fragLocation;
in vec3 FaceNormal;

uniform sampler2D Texture;
uniform vec3 LightPosition;
uniform vec3 ambientLight = vec3(0.5);
uniform vec3 LightColor = vec3(1,1,1);
uniform float 
	LightLinearAttenuation = 0.09,
	LightQuadraticAttenuation = 0.032,
	LightBrightness = 1;

out vec3 outColor;

void main(void) {
	vec4 texcolor = texture(Texture, textureCoord);
	if (texcolor.a < 0.1)
		discard;
	
	// Diffuse
	vec3 tolightdir = LightPosition - fragLocation;
	float distance = length(tolightdir);
	tolightdir =  normalize(tolightdir);
	float directiondiff = max(dot(FaceNormal, tolightdir), 0);
	vec3 diffusecolor = texcolor.rgb * (LightColor * directiondiff);
	// Attenuation
	float attenuation = 1 + LightLinearAttenuation * distance + LightQuadraticAttenuation * (distance * distance);
	float luminosity = 1.0 / attenuation;
	diffusecolor *= luminosity;
	
	// Ambient
	vec3 ambientcolor = texcolor.rgb * ambientLight;
	ambientcolor *= luminosity;
	
	outColor = ambientcolor + diffusecolor;
}