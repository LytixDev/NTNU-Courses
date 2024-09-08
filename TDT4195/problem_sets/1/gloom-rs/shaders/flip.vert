#version 430 core

in vec3 position;

void main()
{
    vec3 flipped = vec3(-1.0f, -1.0f, 1.0f) * position;
    gl_Position = vec4(flipped, 1.0f);
}