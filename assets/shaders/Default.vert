#version 330
precision highp float;

in vec3 position;
in vec3 normal;
in vec2 textcoord01;
in vec3 color;

out vec3 fragmentColor;
out vec3 e_position;
out vec3 e_normal;
out vec2 e_texturecoord;

uniform mat4 mvp;
uniform mat4 modelViewMatrix;
uniform mat4 normalMatrix;

void main() {
	vec4 pos = vec4(position.xyz, 1.0f);

	e_normal = normalize(normalMatrix * vec4(normal, 1.0)).xyz;
	e_position = (modelViewMatrix * pos).xyz;
	e_texturecoord = textcoord01;

	gl_Position =  mvp * pos;
	// converts from -1 to 1 to 0.0 to 1.0
	fragmentColor = ((normal + 1.0) * 0.5);

}

