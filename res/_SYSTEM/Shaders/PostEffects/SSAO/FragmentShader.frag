#version 330

//TODO: Move these to an include file
in vec2 textureCoord;

uniform sampler2D 
	positionTexture,
	normalTexture,
	specularTexture;

uniform float 	zfar;
uniform mat4 	RotationMatrix;
uniform float 	Time;
uniform int 	Samples = 16;
uniform float 	Intensity = 0.5;
uniform float  	Bias = 0.05;
uniform float 	SampleRadius = 0.02;
uniform float 	maxDistance = 0.07;

layout(location=4) out vec3 pixelColor;

#define MOD3 vec3(.1031,.11369,.13787)

float hash12(vec2 p)
{
	vec3 p3  = fract(vec3(p.xyx) * MOD3);
    p3 += dot(p3, p3.yzx + 19.19);
    return fract((p3.x + p3.y) * p3.z);
}

vec2 hash22(vec2 p)
{
	vec3 p3 = fract(vec3(p.xyx) * MOD3);
    p3 += dot(p3, p3.yzx+19.19);
    return fract(vec2((p3.x + p3.y)*p3.z, (p3.x+p3.z)*p3.y));
}

vec3 getPosition(vec2 uv) {
    vec4 position = texture(positionTexture, uv);
	float d = length(position) / zfar;
	
	vec2 p = uv * 2.0 - 1.0;
    mat3 ca = mat3(
		1.0, 0.0, 0.0,
		0.0, 1.0, 0.0,
		0.0, 0.0, -1./1.5
	);
    vec3 rd = normalize( ca * vec3(p, 1.5));
    
	vec3 pos = rd * d;
    return pos;
}

vec3 getNormal(vec2 uv) {
    vec4 normal = texture(normalTexture, uv);
	normal.xyz = normal.xyz * 2.0 - 1.0;
	normal *= RotationMatrix;
	
	return normal.xyz;
}

float doAmbientOcclusion(in vec2 tcoord, in vec2 uv, in vec3 p, in vec3 cnorm)
{
    vec3 diff = getPosition(tcoord + uv) - p;
    float l = length(diff);
    vec3 v = diff / l;
    float ao = max(0.0, dot(cnorm , v)- Bias) * (1.0 / (1.0 + l));
    ao *= smoothstep(maxDistance,maxDistance * 0.5, l);
    
	return ao;
}

float spiralAO(vec2 uv, vec3 p, vec3 n, float initradius)
{
    float goldenAngle = 2.4;
    float ao = 0.;
    float inv = 1. / float(Samples);
    float radius = 0.;

    float rotatePhase = hash12( uv*100. ) * 6.28 * (Time * 8.72);
    float rStep = inv * initradius;
    vec2 spiralUV;

    for (int i = 0; i < Samples; i++) {
        spiralUV.x = sin(rotatePhase);
        spiralUV.y = cos(rotatePhase);
        radius += rStep;
        ao += doAmbientOcclusion(uv, spiralUV * radius, p, n);
        rotatePhase += goldenAngle;
    }
    ao *= inv;
	
    return ao;
}

void main(void) {
	vec2 uv = textureCoord;
	
	vec3 p = getPosition(uv);
    vec3 n = getNormal(uv);
		
    float radius = SampleRadius/p.z;

    float ao = spiralAO(uv, p, n, radius);

    ao *= Intensity;

	
	pixelColor = vec3(1.0 - ao);
}