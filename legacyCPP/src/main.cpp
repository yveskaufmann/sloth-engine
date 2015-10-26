#include <iostream>

#include <gl_stuff.h>
#include <RawModel.h>
#include <Display.h>
#include <DisplayManager.h>
#include <BasicShader.h>

void key_callback(GLFWwindow *window, int key, int scanCode, int action, int mode);

int main() {

	if (glfwInit() != GL_TRUE) {
		throw "GLFW isn't initialized";
	}

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
	RawModel* trianglesModel = RawModel::loadFromFloatArray(vertices, sizeof(vertices) / 3.0);
	// RawModel* trianglesModel = RawModel::loadFromFile("models/teapot.obj");
	BasicShader basicShader;

	glEnable(GL_DEPTH_TEST);
	glDepthFunc(GL_LESS);

	while (! display.shouldClose()) {
		glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
		glEnable(GL_DEPTH);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		basicShader.start();
		basicShader.loadColor(glm::vec3(4.0f, 0.4f, 0.2f));
		basicShader.updateTimer();
		trianglesModel->render();
		display.update();
	}
	
	delete trianglesModel;
	displayManager.clean();
	glfwTerminate();
	
	return 0;
}

void key_callback(GLFWwindow *window, int key, int scanCode, int action, int mode) {
	if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
		glfwSetWindowShouldClose(window, GL_TRUE);
	}
}


