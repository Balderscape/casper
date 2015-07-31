#version 330

layout(std140) uniform;

in vec3 position;
in vec3 normal;
in vec2 texCoord;

out vec3 frag_normal;
out vec3 frag_modelNormal;
out vec3 positionToLight;
out vec3 positionToEye;
out vec2 cubeTexCoord;

uniform Camera {
	vec3 cameraWorldPosition;
	mat4 worldToCameraMatrix; // View Matrix
	mat4 cameraToClipMatrix; // Perspective Matrix
	mat4 worldToClipMatrix; // View-Perspective Matrix
};

uniform Light {
	vec3 lightWorldPosition;
	vec4 lightIntensity;
	vec4 ambientLightIntensity;
	float lightAttenuation;
};

uniform mat4 auto_model;
uniform vec3 auto_lightPosition;

void main() {
	gl_Position = worldToClipMatrix * auto_model * vec4(position, 1);
	vec4 worldPosition = auto_model * vec4(position, 1);
    cubeTexCoord = texCoord;
    frag_normal = normal;
    frag_modelNormal = mat3(auto_model) * normal;
    positionToLight = auto_lightPosition - worldPosition.xyz;
    positionToEye = cameraWorldPosition.xyz - worldPosition.xyz;
}
