#version 150
precision highp float;

in vec3 sl_position;
in vec3 sl_normal;
in vec2 sl_textcoord01;
in vec3 sl_color;

out vec3 position;
out vec3 normal;
out vec2 texturecoord;

uniform mat4 sl_mvp;
uniform mat4 sl_modelViewMatrix;
uniform mat4 sl_normalMatrix;
uniform mat4 sl_modelMatrix;
uniform mat4 sl_viewMatrix;
uniform mat4 sl_projectionMatrix;

void main() {
	vec4 pos = vec4(sl_position.xyz, 1.0f);

	mat3 normalMatrix = transpose(inverse(mat3(sl_modelMatrix)));
	normal = normalize(sl_normalMatrix * vec4(sl_normal, 1.0)).xyz;
	normal = normalize(normalMatrix * sl_normal);
	position = (sl_viewMatrix * pos).xyz;
	texturecoord = sl_textcoord01;

	gl_Position = sl_mvp * pos;
}

