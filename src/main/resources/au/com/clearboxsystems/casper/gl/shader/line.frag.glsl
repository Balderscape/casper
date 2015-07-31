#version 330

layout(std140) uniform;

in FragData {
	flat vec3 cameraStartPos;
	flat vec3 cameraStopPos;
	flat float radius;
	smooth vec2 endMapping;
	smooth float bondPosition;
};

out vec4 outputColor;

uniform float specularShininess;

uniform Camera {
	vec3 cameraWorldPosition;
	mat4 worldToCameraMatrix; // View Matrix
	mat4 cameraToClipMatrix; // Perspective Matrix
	mat4 worldToClipMatrix; // View-Perspective Matrix
};

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
	
	vec3 bondVector = cameraStopPos - cameraStartPos;
	vec3 bondDirection = normalize(bondVector);
	
	vec3 cameraPlanePos = vec3(endMapping * radius, 0.0) + cameraStartPos + bondPosition * bondVector;
	vec3 rayDirection = normalize(cameraPlanePos);
	
	float rayDotV = dot(rayDirection, bondDirection);
	float rayDotP = dot(rayDirection, cameraStartPos);
	float PDotV = dot(cameraStartPos, bondDirection);
	float PDotP = dot(cameraStartPos, cameraStartPos);
	float r2 = radius * radius;

	float B1 = -2.0 * rayDotP;
	float C1 = dot(cameraStartPos, cameraStartPos) - r2;
	
	float det1 = (B1 * B1) - (4 * C1);

	float A2 = 1.0 - (rayDotV * rayDotV); 
	float B2 = 2.0 * ((PDotV * rayDotV) - rayDotP);
	float C2 = PDotP - PDotV * PDotV - r2;

	float det2 = (B2 * B2) - (4 * A2 * C2);
	
	float B3 = 2.0 * dot(rayDirection, -cameraStopPos);
	float C3 = dot(cameraStopPos, cameraStopPos) - r2;
	
	float det3 = (B3 * B3) - (4 * C3);

	float t = 1e20;
	int sphere1 = 0;
	int sphere2 = 0;
	int rod = 0;
	
	if (det1 >= 0) {
		float sqrtDet = sqrt(det1);
		t = min(t, (-B1 + sqrtDet)/2);
		t = min(t, (-B1 - sqrtDet)/2);
		sphere1 = 1;
	}
	
	if (det2 >= 0) {
		float preT = t;
		float sqrtDet = sqrt(det2);
		float tmpT = (-B2 + sqrtDet)/ (2 * A2);
		if ((dot(bondDirection, rayDirection * tmpT - cameraStartPos) > 0) && (dot(bondDirection, rayDirection * tmpT - cameraStopPos) < 0)) {
			t = min(t, tmpT);
		}
		tmpT = (-B2 - sqrtDet)/ (2 * A2);
		if ((dot(bondDirection, rayDirection * tmpT - cameraStartPos) > 0) && (dot(bondDirection, rayDirection * tmpT - cameraStopPos) < 0)) {
			t = min(t, tmpT);
		}
		if (preT != t) {
			rod = 1;
			sphere1 = 0;
		}
	}
	
	if (det3 >= 0) {
		float preT = t;
		float sqrtDet = sqrt(det3);
		t = min(t, (-B3 + sqrtDet)/2);
		t = min(t, (-B3 - sqrtDet)/2);
		if (preT != t) {
			sphere2 = 1;
			rod = 0;
			sphere1 = 0;
		}
	}
	
	if(t >= 1e19)
		discard;		

		
	cameraPos = rayDirection * t;
	if (rod == 1) {
		cameraNormal = normalize((cameraPos - cameraStartPos) - (dot(bondDirection, cameraPos - cameraStartPos) * bondDirection));			
	} else {
		cameraNormal = normalize(cameraPos - (sphere1 * cameraStartPos + sphere2 * cameraStopPos));		
	}
	
	//Set the depth based on the new cameraPos.
    vec4 clipPos = cameraToClipMatrix * vec4(cameraPos, 1.0);
    float ndcDepth = clipPos.z / clipPos.w;
    gl_FragDepth = ((gl_DepthRange.diff * ndcDepth) + gl_DepthRange.near + gl_DepthRange.far) / 2.0;

	vec4 material = vec4(1.0, 1.0, 1.0, 1.0);
	vec4 accumLighting = material * Lgt.ambientIntensity;
	for(int light = 0; light < numberOfLights; light++)
	{
		accumLighting += ComputeLighting(Lgt.lights[light],
			cameraPos, cameraNormal, material);
	}

	outputColor = sqrt(accumLighting); //2.0 gamma correction	
	
//	outputColor = vec4(1.0, 1.0, 1.0, 1.0);
}