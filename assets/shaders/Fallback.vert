#version 130

uniform mat4 sl_mvp;

in vec3 sl_position;

void main() {
	gl_Position = sl_mvp * vec4(sl_position.xyz, 1.0);
}
