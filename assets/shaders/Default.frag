#version 130
precision highp float;

uniform vec4 color = vec4(1.0);
uniform int isWireframe;
uniform sampler2D diffuseTexture;
uniform int sl_light_count;

#define MAX_COLORS 10

uniform struct Light {
	vec4 position;
	vec4 color;
	float attenuation;
	int type;
} sl_lights[MAX_COLORS];

uniform struct Material {
    float ambient;
    float diffuse;
    float shinines;
} sl_material;


in vec3 position;
in vec3 normal;
in vec2 texturecoord;
out vec4 fragmentColor;

vec3 ambientColor = vec3(0.1, 0.2, 0.3);
vec3 diffuseColor = vec3(0.2, 0.2, 0.0);
vec3 specularColor = vec3(0.3, 0.3, 0.3);
float shininess = 0.3 * 2.0;
float ambientIntensity = 0.3;

vec3 calcLightning(Light light, vec3 texelColor, vec3 normal, vec3 pos, vec3 camDir) {

    vec3 lightToPos = light.position.xyz - pos;
	vec3 lightDir = normalize(lightToPos);
	float distanceToLight = length(lightToPos);
    float attenuation = 1.0 / (1.0 + light.attenuation * pow(distanceToLight, 2));

    vec3 ambient = ambientIntensity * texelColor.rgb * light.color.rgb;

    float diffuseIntensity = max(dot(normal, lightDir), 0.0);
    vec3 diffuse = diffuseIntensity * texelColor.rgb * light.color.rgb;

    float specularIntensity = 0.0;
    if (diffuseIntensity > 0.0) {
        vec3 reflection = normalize(-reflect(lightDir, normal));
        specularIntensity = pow(max(dot(reflection, camDir), 0.0), shininess);
     }
     vec3 specular = specularIntensity * specularColor * light.color.rgb;

    return ambient + attenuation * (diffuse + specular);
}

void main() {
	vec4 color = texture(diffuseTexture, texturecoord);

	if (isWireframe == 1) {
		fragmentColor = color;
	} else {
		  vec3 eyePos = normalize(-position.xyz);
		  vec3 texelColor = vec3(0.0);
		  for(int i = 0; i < sl_light_count; i++ ) {
		        texelColor += calcLightning(sl_lights[i], color.rgb, normal, position, eyePos);
		  }

         vec3 gamma = vec3(1.0/2.2);
         fragmentColor = vec4(pow(texelColor, gamma), color.a);
	}
}
