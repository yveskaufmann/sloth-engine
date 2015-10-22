#include <iostream>

#include <gl_stuff.h>
#include <RawModel.h>
#include <Display.h>
#include <DisplayManager.h>
#include <ShaderProgram.h>

void key_callback(GLFWwindow *window, int key, int scanCode, int action, int mode);

int main() {

	const char *vertexShaderSrc =
		"#version 330 core"
		"\n"
		"layout (location = 0) in vec3 position;"
		"\n"
		"void main() {"
		"	gl_Position = vec4(position, 1.0f);"
		"}"
		"\n";

	const char *fragmentShaderSrc =
		"#version 330 core"
		"\n"
		"uniform vec3 in_color;"
		"out vec4 color;"
		"\n"
		"void main() {"
		"	color = vec4(in_color, 1.0f);"
		"}";




	glfwInit();
	DisplayManager displayManager;
	Display& display = displayManager
		.setHeight(1024)
		.setHeight(768)
		.setResizeable(true)
		.build();

	glfwSetKeyCallback(display.getWindow(), key_callback);

	GLfloat vertices[] = {
		-0.5f, -0.5f, 0.0f,
		0.5f, -0.5f, 0.0f,
		0.0f, 0.5,  0.0f,
	};
	RawModel* trianglesModel = RawModel::loadFromFloatArray(vertices, sizeof(vertices));
	ShaderProgram defaultShader(vertexShaderSrc, fragmentShaderSrc);
	

	while (! display.shouldClose()) {
		glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		defaultShader.start();
		trianglesModel->render();
		display.update();
	}
	
	delete trianglesModel;
	
	glfwTerminate();
	
	return 0;
}

void key_callback(GLFWwindow *window, int key, int scanCode, int action, int mode) {
	if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
		glfwSetWindowShouldClose(window, GL_TRUE);
	}
}


