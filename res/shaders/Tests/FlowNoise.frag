#version 330 core

in vec2 fragCoord;

uniform vec2 Resolution;
uniform float Time;

out vec4 fragColor;
// Flow Noise 2d
// by TinyTexel
// Creative Commons Attribution-ShareAlike 4.0 International Public License

/*
Flow noise as described in "Texturing and Modeling A Procedural Approach" (no pseudoadvection for now).
Basic ideas:
- domain distorted fbm ( http://www.iquilezles.org/www/articles/warp/warp.htm )
- rotate gradient kernels to animate noise (faster rotation for higher octaves)

[Perlin, K., and F. Neyret. 2001. Flow noise. SIGGRAPH Technical Sketches and Applications]
*/

///////////////////////////////////////////////////////////////////////////
//=======================================================================//

#define PixelCount Resolution.xy
#define OUT

#define rsqrt inversesqrt
#define clamp01(x) clamp(x, 0.0, 1.0)
#define If(cond, resT, resF) mix(resF, resT, cond)

const float Pi = 3.14159265359;
const float Pi05 = Pi * 0.5;

float Pow2(float x) {return x*x;}
float Pow3(float x) {return x*x*x;}
float Pow4(float x) {return Pow2(Pow2(x));}

vec2 AngToVec(float ang)
{	
	return vec2(cos(ang), sin(ang));
}

float SqrLen(float v) {return v * v;}
float SqrLen(vec2  v) {return dot(v, v);}
float SqrLen(vec3  v) {return dot(v, v);}
float SqrLen(vec4  v) {return dot(v, v);}

float Pow(float x, float e) {return pow(x, e);}
vec2 Pow(vec2 x, float e) {return pow(x, vec2(e));}
vec3 Pow(vec3 x, float e) {return pow(x, vec3(e));}
vec4 Pow(vec4 x, float e) {return pow(x, vec4(e));}

float GammaDecode(float x) {return pow(x,      2.2) ;}
vec2  GammaDecode(vec2  x) {return pow(x, vec2(2.2));}
vec3  GammaDecode(vec3  x) {return pow(x, vec3(2.2));}
vec4  GammaDecode(vec4  x) {return pow(x, vec4(2.2));}

float GammaEncode(float x) {return pow(x,      1.0 / 2.2) ;}
vec2  GammaEncode(vec2  x) {return pow(x, vec2(1.0 / 2.2));}
vec3  GammaEncode(vec3  x) {return pow(x, vec3(1.0 / 2.2));}
vec4  GammaEncode(vec4  x) {return pow(x, vec4(1.0 / 2.2));}

#define FUNC4_UINT(f)								\
uvec2 f(uvec2 v) {return uvec2(f(v.x ), f(v.y ));}	\
uvec3 f(uvec3 v) {return uvec3(f(v.xy), f(v.z ));}	\
uvec4 f(uvec4 v) {return uvec4(f(v.xy), f(v.zw));}	\
    

// single iteration of Bob Jenkins' One-At-A-Time hashing algorithm:
//  http://www.burtleburtle.net/bob/hash/doobs.html
// suggestes by Spatial on stackoverflow:
//  http://stackoverflow.com/questions/4200224/random-noise-functions-for-glsl
uint BJXorShift(uint x) 
{
    x += x << 10u;
    x ^= x >>  6u;
    x += x <<  3u;
    x ^= x >> 11u;
    x += x << 15u;
	
    return x;
}

FUNC4_UINT(BJXorShift)    
    

// xor-shift algorithm by George Marsaglia
//  https://www.thecodingforums.com/threads/re-rngs-a-super-kiss.704080/
// suggestes by Nathan Reed:
//  http://www.reedbeta.com/blog/quick-and-easy-gpu-random-numbers-in-d3d11/
uint GMXorShift(uint x)
{
    x ^= x << 13u;
    x ^= x >> 17u;
    x ^= x <<  5u;
    
    return x;
}

FUNC4_UINT(GMXorShift) 
    
// hashing algorithm by Thomas Wang 
//  http://www.burtleburtle.net/bob/hash/integer.html
// suggestes by Nathan Reed:
//  http://www.reedbeta.com/blog/quick-and-easy-gpu-random-numbers-in-d3d11/
uint WangHash(uint x)
{
    x  = (x ^ 61u) ^ (x >> 16u);
    x *= 9u;
    x ^= x >> 4u;
    x *= 0x27d4eb2du;
    x ^= x >> 15u;
    
    return x;
}

FUNC4_UINT(WangHash) 
    
//#define Hash BJXorShift
#define Hash WangHash
//#define Hash GMXorShift

// "floatConstruct"          | renamed to "ConstructFloat" here 
// By so-user Spatial        | http://stackoverflow.com/questions/4200224/random-noise-functions-for-glsl
// used under CC BY-SA 3.0   | https://creativecommons.org/licenses/by-sa/3.0/             
// reformatted and changed from original to extend interval from [0..1) to [-1..1) 
//-----------------------------------------------------------------------------------------
// Constructs a float within interval [-1..1) using the low 23 bits + msb of an uint.
// All zeroes yields -1.0, all ones yields the next smallest representable value below 1.0. 
float ConstructFloat(uint m) 
{
	float flt = uintBitsToFloat(m & 0x007FFFFFu | 0x3F800000u);// [1..2)
    float sub = (m >> 31u) == 0u ? 2.0 : 1.0;
    
    return flt - sub;// [-1..1)             
}

vec2 ConstructFloat(uvec2 m) { return vec2(ConstructFloat(m.x), ConstructFloat(m.y)); }
vec3 ConstructFloat(uvec3 m) { return vec3(ConstructFloat(m.xy), ConstructFloat(m.z)); }
vec4 ConstructFloat(uvec4 m) { return vec4(ConstructFloat(m.xyz), ConstructFloat(m.w)); }


uint Hash(uint  v, uint  r) { return Hash(v ^ r); }
uint Hash(uvec2 v, uvec2 r) { return Hash(Hash(v.x , r.x ) ^ (v.y ^ r.y)); }
uint Hash(uvec3 v, uvec3 r) { return Hash(Hash(v.xy, r.xy) ^ (v.z ^ r.z)); }
uint Hash(uvec4 v, uvec4 r) { return Hash(Hash(v.xy, r.xy) ^ Hash(v.zw, r.zw)); }

// Pseudo-random float value in interval [-1:1).
float Hash(float v, uint  r) { return ConstructFloat(Hash(floatBitsToUint(v), r)); }
float Hash(vec2  v, uvec2 r) { return ConstructFloat(Hash(floatBitsToUint(v), r)); }
float Hash(vec3  v, uvec3 r) { return ConstructFloat(Hash(floatBitsToUint(v), r)); }
float Hash(vec4  v, uvec4 r) { return ConstructFloat(Hash(floatBitsToUint(v), r)); }


float HashFlt(uint   v, uint  r) { return ConstructFloat(Hash(v, r)); }
float HashFlt(uvec2  v, uvec2 r) { return ConstructFloat(Hash(v, r)); }
float HashFlt(uvec3  v, uvec3 r) { return ConstructFloat(Hash(v, r)); }
float HashFlt(uvec4  v, uvec4 r) { return ConstructFloat(Hash(v, r)); }

uint HashUInt(float v, uint  r) { return Hash(floatBitsToUint(v), r); }
uint HashUInt(vec2  v, uvec2 r) { return Hash(floatBitsToUint(v), r); }
uint HashUInt(vec3  v, uvec3 r) { return Hash(floatBitsToUint(v), r); }
uint HashUInt(vec4  v, uvec4 r) { return Hash(floatBitsToUint(v), r); }


float Root4(float x)
{
    return rsqrt(rsqrt(x));
}

float SCurveCos(float x)
{
    return cos(x * Pi)*-.5+.5;
}

float SCurveC1(float x)
{
    return (x * -2.0 + 3.0) * x*x;
}

float SCurveC2(float x)
{
    return ((x * 6.0 - 15.0) * x + 10.0) * x*x*x;
}

float Sign(float x) {return x < 0.0 ? -1.0 : 1.0;}


#define LOOP(head0, head1, head2, body) {head0 body} {head1 body} {head2 body}

// https://en.wikipedia.org/wiki/Simplex_noise
float SmplxGNoise(vec2 uv, float rotS, uint seed)
{
    // const float n = 2.0;
    const float F = 0.36602540378443860;// (sqrt(n + 1.0) - 1.0) / n;
    const float G = 0.21132486540518708;// (1.0 - rsqrt(n + 1.0)) / n;
    
    vec2 uv2 = uv + (uv.x + uv.y) * F;
    
    vec2 iuv = floor(uv2);
    vec2 fuv = uv2 - iuv;
    
    uvec2 vSeed = uvec2(0x0D66487Cu, 0x9A19276Bu) ^ uvec2(seed);

    float res = 0.0;

    LOOP(vec2 vert = vec2(0.0, 0.0);, 
         vec2 vert = (fuv.x > fuv.y ? vec2(1.0, 0.0) : vec2(0.0, 1.0));, 
         vec2 vert = vec2(1.0, 1.0);,
        
        uint vHash = HashUInt(vert + iuv, vSeed);
         
        float h0 = HashFlt(vHash, 0x7483EC45u);
        float h1 = HashFlt(vHash, 0xE42B9889u);
         
        float ang = h0;
        ang += Time * 3 * rotS * (1.0 + 0.309 * h1);
        
        vec2 g = AngToVec(ang * Pi);

        vec2 vec  = fuv - vert;
             vec -= (vec.x + vec.y) * G;
        
        float w = Pow3(clamp01(1.0 - 2.0 * SqrLen(vec)));
             // w = SCurveC2(clamp01(1.0 - sqrt(2.0)*length(vec)));        
        
        float v = dot(vec, g);
        
        res += w * v;
	)

   
    //res *= 3.36;//SCurveC2
    res *= 4.123;//Pow3
    //res *= 6.2;//Pow4
    
    //res = abs(res*res)*2.0-1.0;
    return res;
}

#undef LOOP


float Fbm(vec2 uv, uint seed)
{
    const float count = 8.0;
    
    float res = 0.0;
    float accu_w = 0.0;
    float w = 1.0;
    float rs = 0.2;
    
    for(float i = 0.0; i < count; ++i)
    {
        float v = SmplxGNoise(uv, rs, seed);
        
        res += v * w;
        
        accu_w += w;
        
        rs *= -1.7;        
        w *= 0.51;
        uv *= 2.2;
        
        seed = GMXorShift(seed);
    }
    
    res /= accu_w;
    
    return res;
}

void main(void)
{     
    vec2 uv = fragCoord.xy - 0.5;
	vec2 tex = fragCoord.xy / PixelCount.xx;
    vec2 tex21 = tex * 2.0 - vec2(1.0);
    
    uint seed0 = 0x17E66082u;
    uint seed1 = 0x0C1BEFEFu;
    uint seed2 = 0xAF6DCAB3u;

    tex *= 4.0;
    
    vec2 o;
    o.x = Fbm(tex + vec2(0.45, 0.13) , seed0)*1.;
    o.y = Fbm(tex + vec2(0.32, 0.87) , seed1)*1.;    
    o = (abs(o)*2.0-1.0) * 0.5;

    float v = Fbm(tex + o * 0.1, seed2); 
    v = v*.5+.5;
    
    fragColor = vec4(vec3(v), 1.0);   
}
