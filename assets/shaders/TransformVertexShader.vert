// Input vertex data, different for all executions of this shader.
in vec3 vertexPosition;
in vec3 vertexColor;

// Output data ; will be interpolated for each fragment.
varying vec3 fragmentColor;
// Values that stay constant for the whole mesh.
uniform mat4 mvp;
uniform float time;
void main() {

	// Output position of the vertex, in clip space : MVP * position
	vec4 pos = vec4(vertexPosition , 1.0f);

	gl_Position =  mvp * pos;

	// The color of each vertex will be interpolated
	// to produce the color of each fragment
	fragmentColor = vertexColor;
}

