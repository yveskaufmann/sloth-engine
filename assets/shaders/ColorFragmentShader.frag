#version 130

// Interpolated values from the vertex shaders
in vec3 fragmentColor;
out vec3 fragColor;


void main(){

	// Output color = color specified in the vertex shader, 
	// interpolated between all 3 surrounding vertices
	fragColor = fragmentColor;

}
