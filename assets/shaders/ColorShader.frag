#version 130

in  vec3 fragmentColor;
uniform float time;

void main(){

	// Output color = color specified in the vertex shader, 
	// interpolated between all 3 surrounding vertices
	gl_FragColor = vec4(fragmentColor, 1.0f);

}
