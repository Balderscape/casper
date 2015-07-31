#version 330

layout(std140) uniform;

in FragData
{
	flat vec3 cameraSpherePos;
	flat float radius;
	flat vec4 material;
	smooth vec2 mapping;
};

out vec4 outputColor;

uniform Camera {
	vec3 cameraWorldPosition;
	mat4 worldToCameraMatrix; // View Matrix
	mat4 cameraToClipMatrix; // Perspective Matrix
	mat4 worldToClipMatrix; // View-Perspective Matrix
};

uniform float specularShininess;

struct PerLight
{
	vec4 cameraSpaceLightPos;
	vec4 lightIntensity;
};

const int numberOfLights = 1;

uniform Light
{
	vec4 ambientIntensity;
	float lightAttenuation;
	PerLight lights[numberOfLights];
} Lgt;

float CalcAttenuation(in vec3 cameraSpacePosition,
	in vec3 cameraSpaceLightPos,
	out vec3 lightDirection)
{
	vec3 lightDifference =  cameraSpaceLightPos - cameraSpacePosition;
	float lightDistanceSqr = dot(lightDifference, lightDifference);
	lightDirection = lightDifference * inversesqrt(lightDistanceSqr);

	return (1 / ( 1.0 + Lgt.lightAttenuation * lightDistanceSqr));
}

vec4 ComputeLighting(in PerLight lightData, in vec3 cameraSpacePosition,
	in vec3 cameraSpaceNormal, in vec4 material)
{
	vec3 lightDir;
	vec4 lightIntensity;
	if(lightData.cameraSpaceLightPos.w == 0.0)
	{
		lightDir = vec3(lightData.cameraSpaceLightPos);
		lightIntensity = lightData.lightIntensity;
	}
	else
	{
		float atten = CalcAttenuation(cameraSpacePosition,
			lightData.cameraSpaceLightPos.xyz, lightDir);
		lightIntensity = atten * lightData.lightIntensity;
	}

	vec3 surfaceNormal = normalize(cameraSpaceNormal);
	float cosAngIncidence = dot(surfaceNormal, lightDir);
	cosAngIncidence = cosAngIncidence < 0.0001 ? 0.0 : cosAngIncidence;

	vec3 viewDirection = normalize(-cameraSpacePosition);

	vec3 halfAngle = normalize(lightDir + viewDirection);
	float angleNormalHalf = acos(dot(halfAngle, surfaceNormal));
	float exponent = angleNormalHalf / specularShininess;
	exponent = -(exponent * exponent);
	float gaussianTerm = exp(exponent);

	gaussianTerm = cosAngIncidence != 0.0 ? gaussianTerm : 0.0;

	vec4 lighting = material * lightIntensity * cosAngIncidence;
	lighting += material * lightIntensity * gaussianTerm;

	return lighting;
}

void main()
{
	vec3 cameraPos;
	vec3 cameraNormal;
	vec3 cameraPlanePos = vec3(mapping * radius, 0.0) + cameraSpherePos;
	vec3 rayDirection = normalize(cameraPlanePos);
	
	float B = 2.0 * dot(rayDirection, -cameraSpherePos);
	float C = dot(cameraSpherePos, cameraSpherePos) -
		(radius * radius);
	
	float det = (B * B) - (4 * C);
	if(det < 0.0)
		discard;		

	float sqrtDet = sqrt(det);
    float posT = (-B + sqrtDet)/2;
    float negT = (-B - sqrtDet)/2;

    float intersectT = min(posT, negT);
    cameraPos = rayDirection * intersectT;
    cameraNormal = normalize(cameraPos - cameraSpherePos);

	//Set the depth based on the new cameraPos.
    vec4 clipPos = cameraToClipMatrix * vec4(cameraPos, 1.0);
    float ndcDepth = clipPos.z / clipPos.w;
    gl_FragDepth = ((gl_DepthRange.diff * ndcDepth) + gl_DepthRange.near + gl_DepthRange.far) / 2.0;

	vec4 accumLighting = material * Lgt.ambientIntensity;
	for(int light = 0; light < numberOfLights; light++)
	{
		accumLighting += ComputeLighting(Lgt.lights[light],
			cameraPos, cameraNormal, material);
	}

	outputColor = sqrt(accumLighting); //2.0 gamma correction
}
