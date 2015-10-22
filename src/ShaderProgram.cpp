//
// Created by fxdapokalypse on 22.10.15.
//
#include <iostream>
#include "ShaderProgram.h"

ShaderProgram::ShaderProgram(const char *vertexShaderSrc, const char *fragmentShaderSrc) throw (ShaderException) {
	compileAndLink(vertexShaderSrc, fragmentShaderSrc);
}

ShaderProgram::~ShaderProgram() {

}

void ShaderProgram::clean() {
	glDeleteProgram(shaderProgram);
}

GLuint ShaderProgram::compileAndLink(const char *vertexShaderSrc, const char *fragmentShaderSrc) throw (ShaderException) {
	GLint success;
	GLuint vertexShader = compileShader(GL_VERTEX_SHADER, vertexShaderSrc);
	GLuint fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentShaderSrc);

	shaderProgram = glCreateProgram();
	glAttachShader(shaderProgram, vertexShader);
	glAttachShader(shaderProgram, fragmentShader);

	glLinkProgram(shaderProgram);
	glGetProgramiv(shaderProgram, GL_LINK_STATUS, &success);
	if (! success) {
		// throw ShaderLinkException
		GLchar infoLog[255];
		glGetProgramInfoLog(shaderProgram, sizeof(infoLog), NULL, infoLog);
		std::cout << "Shader Link Error: " << infoLog << std::endl;
	}

	glValidateProgram(shaderProgram);
	glGetProgramiv(shaderProgram, GL_VALIDATE_STATUS, &success);
	if (! success) {
		GLchar infoLog[255];
		glGetProgramInfoLog(shaderProgram, sizeof(infoLog), NULL, infoLog);
		std::cout << "Shader Validation Error: " << infoLog << std::endl;
	}

	glDeleteShader(vertexShader);
	glDeleteShader(fragmentShader);

	return success;
}

GLuint ShaderProgram::compileShader(GLenum shaderType, const char *shaderSource) throw(ShaderException) {
	GLint success;
	GLuint shader = glCreateShader(shaderType);

	if (shader == GL_INVALID_ENUM) {
		throw std::invalid_argument("ShaderProgram::compileShader invalid shader specified: " + shaderType);
	}

	glShaderSource(shader, 1, &shaderSource, NULL);
	glCompileShader(shader);
	glGetShaderiv(shader, GL_COMPILE_STATUS, &success);
	if (! success) {
		throw ShaderException(shader);
	}

	return shader;
}

void ShaderProgram::start() {
	glUseProgram(shaderProgram);
	GLint colorLocation = glGetUniformLocation(shaderProgram, "in_color");
	glUniform3f(colorLocation, 0.6f, 0.4f , 0.2f);
}

void ShaderProgram::stop() {
	glUseProgram(0);
}

