#version 430 core

in vec4 outColor;
in vec3 outNormal;
out vec4 finalColor;

void main()
{
    vec3 lightDirection = normalize(vec3(0.8, -0.5, 0.6));
    float direction = max(0.0f, dot(outNormal, -lightDirection));
    finalColor.rgb = outColor.rgb * direction;
    finalColor.a = outColor.a;
}