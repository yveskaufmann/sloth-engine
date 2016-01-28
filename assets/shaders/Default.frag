#version 130

in vec3 fragmentColor;
out vec4 fragColor;

uniform int isWireframe;

void main() {

	vec3 color = vec3(fragmentColor);

	if (isWireframe == 1) {
	    color = 1.0 - vec3(0.1, 0.1, 0.1);
	}

	fragColor = vec4(color, 1.0f);

}
