#version 330

layout(std140) uniform;

in vec3 position;
in float radius;
in vec4 material;

uniform float percentRadius;

out VertexData {
	float radius;
	vec4 material;
} outData;

void main()
{
	gl_Position = vec4(position, 1.0);
	outData.radius = percentRadius * radius;
	outData.material = material;
}
