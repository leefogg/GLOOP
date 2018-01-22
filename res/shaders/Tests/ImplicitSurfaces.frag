#version 400 core

in vec2 fragCoord;

uniform vec2 Resolution;
uniform float Time;

out vec4 fragColor;
#define MAX_ITERATIONS 300.f
#define T_MAX 3.5f

const float k_smooth = 24.0;

float Union_SDF( in float sdf1, in float sdf2 )
{
    return min(sdf1, sdf2);
}

// Exponential Smooth Min - from IQ's article: http://www.iquilezles.org/www/articles/smin/smin.htm
float SmoothMin_IQ( in float a, in float b )
{
    float res = exp(-k_smooth * a) + exp(-k_smooth * b);
    return -log(res) / k_smooth;
}

// Signed Distance Functions: http://iquilezles.org/www/articles/distfunctions/distfunctions.htm

float SDF_Sphere( in vec3 pos, in float radius )
{
    return length(pos) - radius;
}

float SDF_Torus( in vec3 pos, in vec2 t)
{
    vec2 qos = vec2(length(pos.xz) - t.x, pos.y);
    return length(qos) - t.y;
}

float SDF_RoundedBox( vec3 pos, vec3 b, float r )
{
  return length(max(abs(pos) - b, 0.0)) - r;
}

float SceneMap( in vec3 pos )
{
    // Bounding Volume
    float distToBoundingSphere = SDF_Sphere(pos, 1.0);
    if(distToBoundingSphere < 0.025f)
    {
        // Cached calls to sin
        float sin_norm1 = (sin(Time) + 1.0) * 0.5; // remap to [0,1]
        float sin_norm2 = (sin(Time * 0.2) + 1.0) * 0.5; // remap to [0,1]
        float sin_norm3 = (sin(Time * 0.6) + 1.0) * 0.5; // remap to [0,1]
        float sin1 = sin(Time * 0.5);
        float sin2 = sin(Time * 1.2);
        float sin3 = sin(Time * 1.5);
        
        float sdf1 = SDF_Sphere(pos - vec3(0.0, sin(Time * 0.25) * 0.3, 0.0), (sin_norm1 * 0.8 + 0.8) * 0.25);
        float sdf2 = SDF_Torus(pos - 0.5 * vec3(0.0, sin1, 0.0), vec2((sin_norm3) * 0.44, 0.025));
        float sdf3 = SDF_Torus(pos - 0.5 * vec3(0.0, sin(Time * 1.7), 0.0), vec2((sin_norm2) * 0.6, 0.05));
        float sdf4 = SDF_Torus(pos - 0.5 * vec3(0.0, sin(Time * 1.4 + 1.0), 0.0), vec2((sin_norm2) * 0.5, 0.06));
        float sdf5 = SDF_Torus(pos - 0.5 * vec3(0.0, sin2, 0.0), vec2((sin_norm3) * 0.8, 0.07));
        float sdf6 = SDF_RoundedBox(pos - 0.5 * vec3(sin1, sin(Time * 1.2), 0.0), vec3(0.025) * (sin_norm1 * 0.5 + 0.75), 0.025);
        float sdf7 = SDF_RoundedBox(pos - 0.5 * vec3(sin(Time * 1.5 + 1.0), sin(Time * 0.5 + 1.0), 0.0), vec3(0.075) * (sin_norm1 * 0.5 + 0.45), 0.025);
        float sdf8 = SDF_RoundedBox(pos - 0.5 * vec3(sin(Time * 0.5 + 0.5), sin(Time * 1.2 + 0.5), 0.0), vec3(0.075) * (sin_norm1 * 0.5 + 0.75), 0.025);
        float sdf9 = SDF_RoundedBox(pos - 0.5 * vec3(sin(Time * 0.5 + 1.0), sin(Time * 1.5 + 0.5), 0.0), vec3(0.075) * (sin_norm1 * 0.5 + 0.45), 0.025);
        float sdf10 = SDF_RoundedBox(pos - 0.5 * vec3(sin2, sin(Time * 0.8 + 0.5), 0.0), vec3(0.075) * (sin_norm1 * 0.5 + 0.75), 0.025);
        
        // Exponential Smooth Min - from IQ's article: http://www.iquilezles.org/www/articles/smin/smin.htm
        sdf1 = exp(-k_smooth * sdf1) +
               exp(-k_smooth * sdf2) +
               exp(-k_smooth * sdf3) +
               exp(-k_smooth * sdf4) +
               exp(-k_smooth * sdf5) +
               exp(-k_smooth * sdf6) +
               exp(-k_smooth * sdf7) +
               exp(-k_smooth * sdf8) +
               exp(-k_smooth * sdf9) +
               exp(-k_smooth * sdf10);
               
        return -log(sdf1) / k_smooth;
    }
    else
    {
        return distToBoundingSphere;
    }
}

vec3 ComputeNormal( in vec3 pos )
{
    vec2 epsilon = vec2(0.0, 0.001);
    return normalize( vec3( SceneMap(pos + epsilon.yxx) - SceneMap(pos - epsilon.yxx),
                            SceneMap(pos + epsilon.xyx) - SceneMap(pos - epsilon.xyx),
                            SceneMap(pos + epsilon.xxy) - SceneMap(pos - epsilon.xxy)));
}


vec3 LightContrib( in vec3 lightDir, in vec3 lightCol, in vec3 normal, in vec3 camLook )
{
    vec3 baseCol = vec3(0.2);
    vec3 specularColor = vec3(0.9);
    float lambertDot = clamp(dot(normal, lightDir), 0.001, 1.0);
    float phongDot = pow(clamp(dot(camLook, reflect(lightDir, normal)), 0.0, 1.0), 40.0);
    
    vec3 diffuseTerm = baseCol * pow(lambertDot, 2.0) * lightCol;
    vec3 specularTerm = 0.1 * phongDot * specularColor;
    
    return lightCol * (diffuseTerm + specularTerm);
}

vec3 ComputeLighting( in vec3 normal, in vec3 camLook )
{
    vec3 accumLight = vec3(0.0);
    
    accumLight += LightContrib(normalize(vec3(0.0, 1.0, -1.0)), vec3(0.92, 0.82, 0.22), normal, camLook);
    accumLight += LightContrib(normalize(vec3(0.5, -0.75, -1.0)), vec3(0.98, 0.8, 0.2), normal, camLook);
    accumLight += LightContrib(normalize(vec3(0.0, -1.0, 1.0)), vec3(0.4, 0.4, 0.8), normal, camLook);
    accumLight += LightContrib(normalize(vec3(-5.0, 2.0, 0.75)), vec3(0.8, 0.8, 0.45) * 1.1, normal, camLook);
    accumLight += LightContrib(normalize(vec3(0.75, 0.25, 0.55)), vec3(0.85, 0.85, 0.42) * 1.1, normal, camLook);
    
    return accumLight;
}

vec3 RaymarchScene( in vec3 origin, in vec3 dir )
{
    float distance;
    float t = 0.01;
    float hitSomething = 0.0;
    
    float i;
    for(i = 0.0; i < MAX_ITERATIONS; i += 1.0)
    {
        distance = SceneMap(origin + t * dir);
        
        if(distance < 0.01)
        {
            hitSomething = 1.0;
            break;
        } else if (t > T_MAX)
        {
            break;
        }
        
        t += distance;
    }
    return vec3(t, hitSomething, i);
}

vec4 cosinePallette(float i)
{
    float r = 0.5f + 0.5f * cos(6.28318 * (0.05f * i + 0.3f));
    float g = 0.5f + 0.5f * cos(6.28318 * (1.0f * i + 0.1f));
    float b = 0.5f + 0.5f * cos(6.28318 * (0.0f * i + 0.1f));
    return vec4(r, g, b, 1.0);
}

void main(void)
{
    vec2 screenPoint = (2.0 * fragCoord.xy - Resolution.xy) / Resolution.y;
    
    // Compute ray direction
    float distance = 2.5;
    vec3 rayOrigin = vec3(cos(Time * 0.5) * distance, 0.0, sin(Time * 0.5) * distance); // camera position
    vec3 rayDirection;
    
    // Ray casting
    vec3 refPoint = vec3(0.0, 0.0, 0.0);
    vec3 camLook = normalize(refPoint - rayOrigin);
    vec3 camRight = normalize(cross(camLook, vec3(0.f, 1.f, 0.f)));
    vec3 camUp = normalize(cross(camRight, camLook));
    
    vec3 rayPoint = refPoint + screenPoint.x * camRight + screenPoint.y * camUp;
    rayDirection = normalize(rayPoint - rayOrigin);
    
    vec3 result = RaymarchScene(rayOrigin, rayDirection);
    
    vec4 finalColor;
    if(result.y > 0.0)
    {
        vec3 normal = ComputeNormal(rayOrigin + result.x * rayDirection);
        finalColor = vec4(ComputeLighting(normal, camLook), 1.f);
    }
    else
    {
        vec2 uv = fragCoord.xy / Resolution.xy;
        float sinThing = sin(Time * 0.175f + 2.f * (uv.x * uv.y));
        float cosThing = cos(Time * 0.175f+ 3.f * (uv.x + uv.y));
        float sinThing2 = sin(Time * 0.125f + 2.f * (length(screenPoint)));
        float colorThing = (sinThing * cosThing * sinThing2) * 0.5f + 0.5f;
        finalColor = vec4(cosinePallette(colorThing) * 0.5f);
    }

    fragColor = vec4(finalColor.xyz, 1);
}