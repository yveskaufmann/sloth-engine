#version 130

in vec3 position;
in vec3 normal;
in vec2 uv;
in vec3 color;

smooth out vec3 fragmentColor;
uniform mat4 mvp;

void main() {
	vec4 pos = vec4(position , 1.0f);
	gl_Position =  mvp * pos;

	// converts from -1 to 1 to 0.0 to 1.0
	fragmentColor = ((normal + 1.0) * 0.5);


}

