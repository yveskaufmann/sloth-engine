#version 130

in vec3 vertexPosition;
in vec3 vertexColor;

out vec3 fragmentColor;
uniform mat4 mvp;

void main() {
	vec4 pos = vec4(vertexPosition , 1.0f);
	gl_Position =  mvp * pos;
	fragmentColor = vertexColor;
}

