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

void main() {
	// TODO: (Shaders 2 P3): Implement fragment texture shader here
	gl_FragColor = vec4(1, 0, 0, 1);
}