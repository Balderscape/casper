#version 330

layout(std140) uniform;

layout (points) in;
layout (triangle_strip) out;
layout (max_vertices = 8) out;

in VertexData {
	vec3 stopPosition;
	float radius;
} vert[];

out FragData {
	flat vec3 cameraStartPos;
	flat vec3 cameraStopPos;
	flat float radius;
	smooth vec2 endMapping;
	smooth float bondPosition;
};

uniform Camera {
	vec3 cameraWorldPosition;
	mat4 worldToCameraMatrix; // View Matrix
	mat4 cameraToClipMatrix; // Perspective Matrix
	mat4 worldToClipMatrix; // View-Perspective Matrix
};

const float g_boxCorrection = 1.5;

void main() {
	vec3 startPosition = gl_in[0].gl_Position.xyz;
	vec3 stopPosition = vert[0].stopPosition;
	vec2 direction = normalize(stopPosition.xy - startPosition.xy);

	vec4 cornerPosition;
	
	float cosA = direction.y;
	float sinA = direction.x;

//  Rotation Matrix, remember constructor is in column first order
//	[cosA, sinA,
//	 -sinA, cosA]
	mat2 rotZ = mat2(cosA, -sinA, sinA, cosA);

	cameraStartPos = startPosition;
	cameraStopPos = stopPosition;
	radius = vert[0].radius;
	endMapping = rotZ * vec2(-1.0, -1.0) * g_boxCorrection;	
	bondPosition = 0.0;
	cornerPosition = vec4(startPosition, 1.0);
	cornerPosition.xy += endMapping * radius;
	gl_Position = cameraToClipMatrix * cornerPosition;
	EmitVertex();

	cameraStartPos = startPosition;
	cameraStopPos = stopPosition;
	radius = vert[0].radius;
	endMapping = rotZ * vec2(1.0, -1.0) * g_boxCorrection;	
	bondPosition = 0.0;
	cornerPosition = vec4(startPosition, 1.0);
	cornerPosition.xy += endMapping * radius;
	gl_Position = cameraToClipMatrix * cornerPosition;
	EmitVertex();

	cameraStartPos = startPosition;
	cameraStopPos = stopPosition;
	radius = vert[0].radius;
	endMapping = rotZ * vec2(-1.0, 0.0) * g_boxCorrection;	
	bondPosition = 0.0;
	cornerPosition = vec4(startPosition, 1.0);
	cornerPosition.xy += endMapping * radius;
	gl_Position = cameraToClipMatrix * cornerPosition;
	EmitVertex();

	cameraStartPos = startPosition;
	cameraStopPos = stopPosition;
	radius = vert[0].radius;
	endMapping = rotZ * vec2(1.0, 0.0) * g_boxCorrection;	
	bondPosition = 0.0;
	cornerPosition = vec4(startPosition, 1.0);
	cornerPosition.xy += endMapping * radius;
	gl_Position = cameraToClipMatrix * cornerPosition;
	EmitVertex();

	cameraStartPos = startPosition;
	cameraStopPos = stopPosition;
	radius = vert[0].radius;
	endMapping = rotZ * vec2(-1.0, 0.0) * g_boxCorrection;	
	bondPosition = 1.0;
	cornerPosition = vec4(stopPosition, 1.0);
	cornerPosition.xy += endMapping * radius;
	gl_Position = cameraToClipMatrix * cornerPosition;
	EmitVertex();
	
	cameraStartPos = startPosition;
	cameraStopPos = stopPosition;
	radius = vert[0].radius;
	endMapping = rotZ * vec2(1.0, 0.0) * g_boxCorrection;	
	bondPosition = 1.0;
	cornerPosition = vec4(stopPosition, 1.0);
	cornerPosition.xy += endMapping * radius;
	gl_Position = cameraToClipMatrix * cornerPosition;
	EmitVertex();
	
	cameraStartPos = startPosition;
	cameraStopPos = stopPosition;
	radius = vert[0].radius;
	endMapping = rotZ * vec2(-1.0, 1.0) * g_boxCorrection;	
	bondPosition = 1.0;
	cornerPosition = vec4(stopPosition, 1.0);
	cornerPosition.xy += endMapping * radius;
	gl_Position = cameraToClipMatrix * cornerPosition;
	EmitVertex();
	
	cameraStartPos = startPosition;
	cameraStopPos = stopPosition;
	radius = vert[0].radius;
	endMapping = rotZ * vec2(1.0, 1.0) * g_boxCorrection;	
	bondPosition = 1.0;
	cornerPosition = vec4(stopPosition, 1.0);
	cornerPosition.xy += endMapping * radius;
	gl_Position = cameraToClipMatrix * cornerPosition;
	EmitVertex();
	
	EndPrimitive();
}
