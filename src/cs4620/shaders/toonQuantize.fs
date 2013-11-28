
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

// TODO: (Shaders 1 Problem 1) Declare any varying variables here
varying vec3 ex_Normal;
varying vec4 ex_EyeSpacePosition;

void main()
{
	// TODO: (Shaders 1 Problem 1) Implement the fragment shader for
	// the toon quantization shader here
    float intensity = 0;
	vec3 vIntensity = vec3(0.0,0.0,0.0);
	vec3 unitToLight = vec3(0.0,0.0,0.0);
	vec3 unitNormal = normalize(ex_Normal);
	vec3 colorRGB = vec3(0.0,0.0,0.0);

	// ********
	// We use vIntensity to take into all light sources
	// ON PURPOSE. A better toon shader.
	// Performs the same as the conventional quantize for one
	// light source when given one light.
	// But takes into account all light sources instead
	// such that the user has much more artistic freedom.
	// PROFESSOR MARSCHNER APPROVED THIS EXTENSION OF TOON SHADER QUANTIZE
	// --> See ArtBall.txt in the main shaders1_student directory for an example.
	
	for (int i = 0; i < 16; i++)
	{
		unitToLight = normalize(un_LightPositions[i] - ex_EyeSpacePosition.xyz);
		
		intensity = dot(unitNormal, unitToLight);

		if (intensity<=0){
		   vIntensity = vIntensity+(0)*un_DiffuseColor;
		}
		
		else if (intensity<=0.5){
		   vIntensity = vIntensity+(0.25)*un_LightIntensities[i];
		}

		else if (intensity<=0.75){
		   vIntensity = vIntensity+(0.50)*un_LightIntensities[i];
		}

		else{
		   vIntensity = vIntensity+(0.75)*un_LightIntensities[i];
		}
		
		colorRGB = vIntensity*un_DiffuseColor;// add stuff to right for cool looks //* clamp(dot(unitNormal, unitToLight), 0.0, 1.0);
	}
	gl_FragColor = vec4(colorRGB, 1.0f);
}
