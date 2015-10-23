//
// Created by fxdapokalypse on 23.10.15.
//

#include "BasicShader.h"

const char *vertexShaderSrc =
	"#version 330 core"
		"\n"
		"layout (location = 0) in vec3 position;"
		"uniform mat4 test;"
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

BasicShader::BasicShader() : ShaderProgram() {}

void BasicShader::init() throw(ShaderException) {
	compileAndLink(vertexShaderSrc, fragmentShaderSrc);
}

void BasicShader::loadAllUniformLocations() {
	colorUniformLocation = getUniformLocation("in_color");
}

void BasicShader::bindAllAttributeLocations() {
	bindAttributeLocation("position", 0);
}

void BasicShader::loadColor(glm::vec3 color) {
	setUniform(colorUniformLocation, color);
}





