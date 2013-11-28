
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

// TODO: (Shaders 1 Problem 2) Declare any varying variables here
varying vec4 ex_EyeSpacePosition;
varying vec3 ex_Normal;

void main()
{
	// TODO: (Shaders 1 Problem 2) Implement the fragment shader for the flower shader here
	
	//initialize vectors
	vec3 unitToLight = vec3(0.0,0.0,0.0);
	vec3 unitEyePos = normalize(vec3(-ex_EyeSpacePosition.xyz));
	vec3 unitNormal = normalize(ex_Normal);
	vec3 unitH = vec3(0.0,0.0,0.0);
	
	//ambient
	vec3 ambient = un_AmbientColor * un_LightAmbientIntensity;
	vec3 diffusePart = ambient;
	vec3 specPart = vec3(0.0,0.0,0.0);
	vec3 colorRGB = vec3(0.0,0.0,0.0);
	     
	//for each light source
	for (int i = 0; i < 16; i++)
	{
		
		unitToLight = normalize(un_LightPositions[i]- ex_EyeSpacePosition.xyz);
		unitH = normalize((unitEyePos+unitToLight));
		
		if ( dot(unitNormal, unitToLight) >= 0){
		//diffuse part 
	
		diffusePart = diffusePart + un_LightIntensities[i] * un_DiffuseColor * clamp(dot(unitNormal, unitToLight), 0.0, 1.0);
	
		//specular part			
		specPart = specPart + un_LightIntensities[i]*un_SpecularColor* pow(clamp(dot(unitNormal,unitH),0.0, 1.0), un_Shininess);
		}
		else {
		diffusePart = diffusePart;
		specPart = specPart;
		}
		//result
		colorRGB = diffusePart + specPart;	
	}
		gl_FragColor = vec4(colorRGB, 1.0f);
		
}

