#version 330

layout(std140) uniform;

in vec3 startPosition;
in vec3 stopPosition;

out VertexData {
	vec3 stopPosition;
	float radius;
} outData;

uniform Camera {
	vec3 cameraWorldPosition;
	mat4 worldToCameraMatrix; // View Matrix
	mat4 cameraToClipMatrix; // Perspective Matrix
	mat4 worldToClipMatrix; // View-Perspective Matrix
};

void main()
{
	gl_Position = worldToCameraMatrix * vec4(startPosition, 1.0);
	outData.stopPosition = (worldToCameraMatrix * vec4(stopPosition, 1.0)).xyz;
	outData.radius = 10;
}
