#version 330
precision highp float;

in vec3 sl_position;
in vec3 sl_normal;
in vec2 sl_textcoord01;
in vec3 sl_color;

out vec3 position;
out vec3 normal;
out vec2 texturecoord;

uniform mat4 mvp;
uniform mat4 modelViewMatrix;
uniform mat4 normalMatrix;

void main() {
	vec4 pos = vec4(sl_position.xyz, 1.0f);

	normal = normalize(normalMatrix * vec4(sl_normal, 1.0)).xyz;
	position = (modelViewMatrix * pos).xyz;
	texturecoord = sl_textcoord01;

	gl_Position =  mvp * pos;
}

