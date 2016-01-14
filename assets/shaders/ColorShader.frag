#version 130

smooth in vec3 fragmentColor;
uniform float time;

out vec4 fragColor;

void main(){

	// Output color = color specified in the vertex shader, 
	// interpolated between all 3 surrounding vertices
	fragColor = vec4(fragmentColor, 1.0f);

}
