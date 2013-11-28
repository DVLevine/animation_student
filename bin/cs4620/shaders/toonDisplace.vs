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

// How far the object's surface should be displaced (in eye space)
uniform float displaceScale;

// vertex attributes -- distinct value used for each vertex
attribute vec3 in_Vertex;
attribute vec3 in_Normal;

void main()
{
	// TODO: (Shaders 1 Problem 1) Implement the vertex shader for
	// the toon displacement shader here
    gl_Position = un_Projection * un_ModelView * vec4(in_Vertex, 1);
}
