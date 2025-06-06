#version 430 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec4 color;

out vec4 outColor;

uniform mat4 projection;

void main()
{
    gl_Position = projection * vec4(position, 1.0f);
    outColor = color;
}