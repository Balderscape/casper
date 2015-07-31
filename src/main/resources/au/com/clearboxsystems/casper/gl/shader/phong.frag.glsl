#version 330

in vec3 frag_normal;
in vec3 frag_modelNormal;
in vec3 positionToLight;
in vec3 positionToEye;
in vec2 cubeTexCoord;
out vec3 fragmentColor;

uniform vec3 u_color;
uniform bool u_textured;
uniform sampler2D u_texture0;
uniform vec4 auto_diffuseSpecularAmbientShininess;

float LightIntensity(vec3 normal, vec3 toLight, vec3 toEye, vec4 diffuseSpecularAmbientShininess)
{
    vec3 toReflectedLight = reflect(-toLight, normal);

    float diffuse = max(dot(toLight, normal), 0.0);
    float specular = max(dot(toReflectedLight, toEye), 0.0);
    specular = pow(specular, diffuseSpecularAmbientShininess.w);

    return (diffuseSpecularAmbientShininess.x * diffuse) +
            (diffuseSpecularAmbientShininess.y * specular) +
            diffuseSpecularAmbientShininess.z;
}

vec2 ComputeTextureCoordinates(vec3 normal)
{
	float PI = 3.14159265358979323846264;
	float oneOverPi = 1 / PI;
	float oneOverTwoPi = 1 / (2 * PI);
    return vec2(atan(normal.y, normal.x) * oneOverTwoPi + 0.5, -asin(normal.z) * oneOverPi + 0.5);
}

void main() {
    float intensity = LightIntensity(frag_modelNormal,  normalize(positionToLight), normalize(positionToEye), auto_diffuseSpecularAmbientShininess);

	if (u_textured)
	{
		fragmentColor = intensity * texture(u_texture0,cubeTexCoord).rgb;

	}
	else
	{
		fragmentColor = intensity * u_color;
	}
}

