//
// Created by fxdapokalypse on 21.10.15.
//

#include <algorithm>

#include "Display.h"

Display::Display(GLFWwindow *window, std::string title)
	: window(window), title(title) {

};

Display::~Display() { }
Display& Display::setTitle(std::string title) {
	this->title;
	glfwSetWindowTitle(window, title.c_str());
	return *this;
}

std::string Display::getTitle() {
	return this->title;
}

Display& Display::setWidth(int width) {
	setSize(width, getHeight());
}

int Display::getWidth() {
	int width;
	glfwGetWindowSize(window, &width, NULL);
	return width;
}

Display& Display::setHeight(int height) {
	setSize(getWidth(), height);
}

int Display::getHeight() {
	int height;
	glfwGetWindowSize(window, NULL, &height);
	return height;
}

Display& Display::setSize(int width, int height) {
	width = std::max(width, 1);
	height = std::max(height, 1);
	glfwSetWindowSize(window, width, height);
	glViewport(0, 0, width, height);
}

GLFWwindow* Display::getWindow() const {
	return window;
}

bool Display::shouldClose() const {
	return glfwWindowShouldClose(window);
}

void Display::update() {
	glfwSwapBuffers(window);
	glfwPollEvents();
}

void Display::enable() {
	glfwMakeContextCurrent(window);
	glViewport(0, 0, getWidth(), getHeight());
	glfwSetWindowSizeCallback(window, [] (GLFWwindow* window, int width, int height ) {
		width = std::max(width, 10);
		height = std::max(height, 10);
		glViewport(0, 0, width, height);
	});
}

void Display::disable() {
	glfwSetWindowSizeCallback(window, NULL);
	glfwMakeContextCurrent(NULL);
}
