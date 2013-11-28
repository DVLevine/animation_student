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

uniform sampler2D un_Texture;

// TODO (Shaders 2 P3): Declare any varying variables here
varying vec3 ex_Normal;
varying vec4 ex_EyeSpacePosition;
varying vec2 ex_TexCoord;

void main() {
	// TODO: (Shaders 2 P3): Implement fragment texture shader here
	
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
		gl_FragColor = vec4(diffusePart, 1.0f)*texture2D(un_Texture, ex_TexCoord)+vec4(specPart,1.0f);
}