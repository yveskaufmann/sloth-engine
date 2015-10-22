//
// Created by fxdapokalypse on 21.10.15.
//
#include <iostream>
#include "DisplayManager.h"

const int DEFAULT_SCREEN_WIDTH = 1024;
const int DEFAULT_SCREEN_HEIGHT = 768;
const int DEFAULT_OPEN_GL_MAJOR_VERSION = 3;
const int DEFAULT_OPEN_GL_MINOR_VERSION = 3;
const std::string DEFAULT_TITLE = "Untitled";

DisplayManager::DisplayManager() {
	reset();
};


DisplayManager::~DisplayManager() {
	clean();
};

DisplayManager& DisplayManager::reset() {
	windowHints.clear();
	setGLContextVersion(DEFAULT_OPEN_GL_MAJOR_VERSION, DEFAULT_OPEN_GL_MINOR_VERSION);
	setWidth(DEFAULT_SCREEN_WIDTH);
	setHeight(DEFAULT_SCREEN_HEIGHT);
	setTitle(DEFAULT_TITLE);
	glfwDefaultWindowHints();
	return *this;
}

void DisplayManager::clean() {
	reset();

	if (display != nullptr) {
		display->disable();
		display = nullptr;
		delete display;
	}

	if (window != nullptr) {
		glfwDestroyWindow(window);
		window = nullptr;
	}

	windowHints.clear();
}

DisplayManager& DisplayManager::setHeight(int height) {
	this->height = height;
	return *this;
}

DisplayManager& DisplayManager::setWidth(int width) {
	this->width = width;
	return *this;
}

DisplayManager& DisplayManager::setTitle(std::string title) {
	this->title = title;
	return *this;
}

DisplayManager& DisplayManager::enableWindowAutoIconify(bool enabled) {
	return toggleWindowHint(GLFW_AUTO_ICONIFY, enabled);
}

DisplayManager& DisplayManager::enableWindowFloating(bool enable) {
	return toggleWindowHint(GLFW_FLOATING, enable);
}

DisplayManager& DisplayManager::setFocused(bool focused) {
	return toggleWindowHint(GLFW_FOCUSED, focused);
}

DisplayManager& DisplayManager::setResizeable(bool resizeable) {
	return toggleWindowHint(GLFW_RESIZABLE, resizeable);
}

DisplayManager& DisplayManager::setGLContextVersion(int major, int minor) {
	setWindowHint(GLFW_CONTEXT_VERSION_MAJOR, major);
	setWindowHint(GLFW_CONTEXT_VERSION_MINOR, minor);
	return *this;
}

DisplayManager& DisplayManager::setWindowHint(int hint, int value) {
	windowHints[hint] = value;
}

DisplayManager& DisplayManager::toggleWindowHint(int hint, bool state) {
	if (! state) {
		windowHints.erase(hint);
	} else {
		setWindowHint(hint, 1);
	}
	return *this;
}

Display& DisplayManager::build() throw(DisplayException) {

	for (auto windowHint : windowHints) {
		glfwWindowHint(windowHint.first, windowHint.second);
	}

	glfwSetErrorCallback([](int errorCode, const char* description) {
		std::cerr << description << std::endl;
	});

	window = glfwCreateWindow(width, height, title.c_str(), nullptr, nullptr);
	if (window == nullptr) {
		throw DisplayException("Failed to create a display.");
	}

	display = new Display(window, title);
	display->enable();

	glewExperimental = true;
	if (glewInit() != GLEW_OK) {
		throw DisplayException("Failed to initialize glew.");
	}

	reset();
	return *display;
}




