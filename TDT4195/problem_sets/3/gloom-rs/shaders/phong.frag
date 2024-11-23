#version 430 core

in vec4 outColor;  // Color coming from vertex shader
in vec3 outNormal; // Normal coming from vertex shader
in vec3 fragPosition; // Fragment position in world coordinates
in vec3 viewPosition; // Camera position in world coordinates

out vec4 finalColor;

void main()
{
    // Same as the lambertian shading example
    vec3 lightDirection = normalize(vec3(0.8, -0.5, 0.6));

    // Light properties
    vec3 lightColor = vec3(1.0, 0.95, 0.85);
    vec3 ambientLight = vec3(0.05, 0.05, 0.05);
    // Material properties
    float shininess = 50.0;
    vec3 specularColor = vec3(1.0, 1.0, 1.0);
    // Direction from the fragment to the camera
    vec3 viewDir = normalize(viewPosition - fragPosition); 

    // Ambient
    vec3 ambient = ambientLight * outColor.rgb;

    // Diffuse
    float direction = max(0.0f, dot(outNormal, -lightDirection));
    vec3 diffuse = direction * lightColor * outColor.rgb;

    // Specular component / Phong reflection
    // Reflection of the light direction around the normal. Thakfully, GLSL has a built-in function for that :-)
    vec3 reflectDir = reflect(lightDirection, outNormal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
    vec3 specular = spec * specularColor * lightColor;

    vec3 phongLighting = ambient + diffuse + specular;
    finalColor = vec4(phongLighting, outColor.a);
}
