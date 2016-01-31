#version 130
precision highp float;

uniform int isWireframe;
uniform sampler2D diffuseTexture;

in vec3 e_position;
in vec3 e_normal;
in vec3 fragmentColor;
in vec2 e_texturecoord;
out vec4 fragColor;

vec3 lightPos = vec3(0.0, 0.0, -2.5);
vec3 ambientColor = vec3(0.1, 0.2, 0.3);
vec3 diffuseColor = vec3(0.2, 0.2, 0.0);
vec3 specularColor = vec3(0.3, 0.3, 0.3);
float shininess = 2.0;

void main() {


    vec4 color = texture(diffuseTexture, e_texturecoord);


	if (isWireframe == 1) {
	    fragColor = vec4(0.9, 0.9, 0.9, 1.0);
	} else {
          vec3 lightDir = normalize(lightPos - e_position);
          vec3 eyePos = normalize(-e_position);
          vec3 reflection = normalize(-reflect(lightDir, e_normal));

          float intensity = max(dot(e_normal, lightDir), 0.0);

          vec3 lightDiffuseColor = diffuseColor * intensity;
          lightDiffuseColor = clamp(lightDiffuseColor, 0.0, 1.0);

          vec3 lightSpecularColor = specularColor * pow(max(dot(reflection, eyePos), 0.0),  0.3 * shininess);

            // * intensity + ambientColor + lightSpecularColor
          fragColor = vec4(color.rgb * intensity, 1.0);
	}
}
