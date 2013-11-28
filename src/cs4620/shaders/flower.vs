
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

// Transform between the object's local frame and
// the "yellow" light-aligned frame in Figures 5/6/7
uniform mat4 un_FrameToObj;
uniform mat4 un_ObjToFrame;

// TODO: (Shaders 1 Problem 2) Declare any additional uniform variables here
uniform float un_FlowerHeight;
uniform float un_Phi;
uniform float un_Radius;

// vertex attributes -- distinct value used for each vertex
attribute vec3 in_Vertex;
attribute vec3 in_Normal;

// TODO: (Shaders 1 Problem 2) Declare any varying variables here
varying vec4 ex_EyeSpacePosition;
varying vec3 ex_Normal;

void main()
{
	// TODO: (Shaders 1 Problem 2) Implement the vertex shader for the
	// flower shader here
	
	vec4 vFrame = un_ObjToFrame * vec4(in_Vertex, 1);
	vec4 nFrame = un_ObjToFrame * vec4(in_Normal, 1);
	
	float phiPrime = vFrame.y / un_FlowerHeight * un_Phi;
	float rPrime = un_Radius - vFrame.x;
	
	float delX = rPrime * (1 - cos(phiPrime));
	
	float xPrime = vFrame.x + delX;
	float yPrime = rPrime * sin(phiPrime);
	
	ex_EyeSpacePosition = un_ModelView * un_FrameToObj * vec4(xPrime, yPrime, vFrame.z, 1);
	gl_Position = un_Projection * ex_EyeSpacePosition;
	
	mat4 rotate = mat4( cos(-phiPrime), sin(-phiPrime), 0, 0,
					-sin(-phiPrime), cos(-phiPrime), 0, 0,
					0, 0, 1, 0,
					0, 0, 0, 1 );
	
	vec4 nRotated = un_FrameToObj * rotate * nFrame;
	
	ex_Normal = normalize(un_NormalMatrix * nRotated.xyz);
}

