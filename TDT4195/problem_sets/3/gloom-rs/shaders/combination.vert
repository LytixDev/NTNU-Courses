#version 430 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec4 color;
layout(location = 2) in vec3 normal;
layout(location = 3) uniform mat4 projection;
layout(location = 4) uniform mat4 model_matrix;
layout(location = 5) uniform vec3 viewPos;

out vec4 outColor;
out vec3 outNormal;
out vec3 fragPosition;
out vec3 viewPosition;

void main()
{
    outColor = color;
    outNormal = normalize(mat3(model_matrix) * normal);
    gl_Position = projection * vec4(position, 1.0f);
    fragPosition = vec3(model_matrix * vec4(position, 1.0f));
    viewPosition = viewPos;
}