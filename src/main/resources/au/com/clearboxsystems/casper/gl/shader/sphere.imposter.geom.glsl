#version 330

layout(std140) uniform;

layout (points) in;
layout (triangle_strip) out;
layout (max_vertices = 4) out;

in VertexData {
	float radius;
	vec4 material;
} vert[];

out FragData {
	flat vec3 cameraSpherePos;
	flat float radius;
	flat vec4 material;
	smooth vec2 mapping;
};

uniform Camera {
	vec3 cameraWorldPosition;
	mat4 worldToCameraMatrix; // View Matrix
	mat4 cameraToClipMatrix; // Perspective Matrix
	mat4 worldToClipMatrix; // View-Perspective Matrix
};

const float g_boxCorrection = 1.5;

void main() {
	vec3 position = gl_in[0].gl_Position.xyz;

	vec4 cameraPosition = worldToCameraMatrix * vec4(position, 1.0);
	vec4 cornerPosition;

	radius = vert[0].radius;
	material = vert[0].material;
	cameraSpherePos = cameraPosition.xyz;
	mapping = vec2(-1.0, -1.0) * g_boxCorrection;
	cornerPosition = vec4(cameraPosition);
	cornerPosition.xy += mapping * radius;
	gl_Position = cameraToClipMatrix * cornerPosition;
	EmitVertex();

	radius = vert[0].radius;
	material = vert[0].material;
	cameraSpherePos = cameraPosition.xyz;
	mapping = vec2(1.0, -1.0) * g_boxCorrection;
	cornerPosition = vec4(cameraPosition);
	cornerPosition.xy += mapping * radius;
	gl_Position = cameraToClipMatrix * cornerPosition;
	EmitVertex();

	radius = vert[0].radius;
	material = vert[0].material;
	cameraSpherePos = cameraPosition.xyz;
	mapping = vec2(-1.0, 1.0) * g_boxCorrection;
	cornerPosition = vec4(cameraPosition);
	cornerPosition.xy += mapping * radius;
	gl_Position = cameraToClipMatrix * cornerPosition;
	EmitVertex();

	radius = vert[0].radius;
	material = vert[0].material;
	cameraSpherePos = cameraPosition.xyz;
	mapping = vec2(1.0, 1.0) * g_boxCorrection;
	cornerPosition = vec4(cameraPosition);
	cornerPosition.xy += mapping * radius;
	gl_Position = cameraToClipMatrix * cornerPosition;
	EmitVertex();

	EndPrimitive();
}