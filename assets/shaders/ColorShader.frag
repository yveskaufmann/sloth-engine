#version 130

varying vec3 fragmentColor;
uniform float time;

void main(){

	// Output color = color specified in the vertex shader, 
	// interpolated between all 3 surrounding vertices
	gl_FragColor = vec4(fragmentColor + sin(time) * 0.2 , 1.0f);

}
