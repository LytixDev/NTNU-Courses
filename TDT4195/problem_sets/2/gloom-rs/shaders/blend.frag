#version 430 core

in vec4 outColor;
out vec4 finalColor;

void main()
{
    finalColor = outColor; // NOTE: OpenGL will automatically blend the colors for us
}