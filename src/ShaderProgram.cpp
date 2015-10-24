//
// Created by fxdapokalypse on 22.10.15.
//
#include <iostream>
#include "ShaderProgram.h"


ShaderProgram::ShaderProgram()
	: alreadyInitialized(false) {
}

ShaderProgram::~ShaderProgram() {

}

void ShaderProgram::clean() {
	glDeleteProgram(shaderProgram);
	alreadyInitialized = false;
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

	if ( success) {
		loadAllUniformLocations();
		bindAllAttributeLocations();
	}

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
	if (! alreadyInitialized) {
		init();
		alreadyInitialized = true;
	}
	glUseProgram(shaderProgram);
}

void ShaderProgram::stop() {
	glUseProgram(0);
}

void ShaderProgram::bindAttributeLocation(const char *attributeName, GLuint location) {
	glBindAttribLocation(shaderProgram, location, attributeName);
}

GLuint ShaderProgram::getUniformLocation(const char *name) {
	GLint location = glGetUniformLocation(shaderProgram, name);

	if (location == -1) {
		std::string variableName(name);
		throw new ShaderException("The specified variable name \"" + variableName + "\" doesn't correspond to a valid uniform variable");
	}

	return location;
}

ShaderProgram& ShaderProgram::setUniform(GLuint location, GLfloat value) {
	glUniform1f(location, value);
	return *this;
}

ShaderProgram& ShaderProgram::setUniform(GLuint location, GLdouble value) {
	glUniform1d(location, value);
	return  *this;
}

ShaderProgram& ShaderProgram::setUniform(GLuint location, GLint value) {
	glUniform1i(location, value);
	return *this;
}

ShaderProgram& ShaderProgram::setUniform(GLuint location, GLuint value) {
	glUniform1ui(location, value);
	return *this;
}

ShaderProgram& ShaderProgram::setUniformCurrentTime(GLuint location) {
	GLfloat currentTime = (GLfloat) glfwGetTime();
	setUniform(location, currentTime);
	return  *this;
}

ShaderProgram& ShaderProgram::setUniform(GLuint location, glm::mat4 matrix) {
	glUniformMatrix4fv(location, 1, GL_FALSE, &matrix[0][0]);
	return *this;
}

ShaderProgram& ShaderProgram::setUniform(GLuint location, glm::vec2 vector) {
	glUniform2fv(location, 1, &vector[0]);
	return *this;
}

ShaderProgram& ShaderProgram::setUniform(GLuint location, glm::vec3 vector) {
	glUniform3fv(location, 1, &vector[0]);
	return *this;
}

ShaderProgram& ShaderProgram::setUniform(GLuint location, glm::vec4 vector) {
	glUniform4fv(location, 1, &vector[0]);
	return *this;
}






