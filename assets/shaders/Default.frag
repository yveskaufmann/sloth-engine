#version 130

in vec3 fragmentColor;
out vec4 fragColor;

uniform int isWireframe;

void main() {
	// Output color = color specified in the vertex shader, 
	// interpolated between all 3 surrounding vertices

	vec3 color = vec3(fragmentColor.r * 0.5);

	if (isWireframe == 1) {
	    color = 1.0 - vec3(0.1, 0.1, 0.1);
	}

	fragColor = vec4(color, 1.0f);

}
