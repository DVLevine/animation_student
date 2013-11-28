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

uniform sampler2D un_NoiseTexture;
uniform sampler2D un_FireTexture;
uniform float un_Time;

uniform vec3 un_ScrollSpeeds;

// TODO (Shaders 2 P3): Declare varying variables here
varying vec3 ex_Normal;
varying vec4 ex_EyeSpacePosition;
varying vec2 ex_TexCoord;

void main() {
	// TODO (Shaders 2 P3): Implement the fire fragment shader here
	float x1, y1, x2, y2, x3, y3;
	x1 = mod(ex_TexCoord.x, 1);
	x2 = mod(2 * ex_TexCoord.x, 1);
	x3 = mod(3 * ex_TexCoord.x, 1);
	y1 = mod(ex_TexCoord.y + un_Time * un_ScrollSpeeds.x, 1);
	y2 = mod(2 * ex_TexCoord.y + un_Time * un_ScrollSpeeds.y, 1);
	y3 = mod(3 * ex_TexCoord.y + un_Time * un_ScrollSpeeds.z, 1);
	
	vec4 sample1, sample2, sample3;
	sample1 = texture2D(un_NoiseTexture, vec2(x1,y1));
	sample2 = texture2D(un_NoiseTexture, vec2(x2,y2));
	sample3 = texture2D(un_NoiseTexture, vec2(x3,y3));
	
	float avgx, avgy;
	avgx = (sample1.x + sample2.x + sample3.x)/3;
	avgy = (sample1.y + sample2.y + sample3.y)/3;
	
	vec4 texture = texture2D(un_FireTexture, vec2(avgx,avgy));
	gl_FragColor = texture;
}