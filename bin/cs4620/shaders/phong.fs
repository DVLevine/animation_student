#version 120

// uniforms -- same value is used for every vertex in model
uniform mat4 un_Projection;
uniform mat4 un_ModelView;
uniform mat3 un_NormalMatrix;

uniform vec3 un_AmbientColor;
uniform vec3 un_DiffuseColor;
uniform vec3 un_SpecularColor;
uniform float un_Shininess;

uniform vec3 un_LightPositions[16];
uniform vec3 un_LightIntensities[16];
uniform vec3 un_LightAmbientIntensity;

// TODO: (Shaders 1 Problem 1) Specify any varying variables here

void main(void)
{
	// TODO: (Shaders 1 Problem 1) Implement the fragment shader for per-pixel
	// Blinn-Phong here
	gl_FragColor = vec4(1, 0, 0, 1);
}

