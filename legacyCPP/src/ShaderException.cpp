//
// Created by fxdapokalypse on 22.10.15.
//

#include "ShaderException.h"
#include <iostream>
#include <sstream>

ShaderException::ShaderException(std::string message) : runtime_error(message),message(message) {
}

ShaderException::ShaderException(GLuint shader) : runtime_error(""), shader(shader) {
	GLchar *infoLog = NULL;
	GLint shaderType;
	GLint infoLogLength;
	std::stringstream ss;

	glGetShaderiv(shader, GL_SHADER_TYPE, &(shaderType));
	glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLogLength);

	ss << "Error::Shader::";
	switch (shaderType) {
		case GL_VERTEX_SHADER: ss << "VertexShader"; break;
		case GL_FRAGMENT_SHADER: ss <<"FragmentShader";break;
		case GL_GEOMETRY_SHADER: ss <<"GeometryShader"; break;
		case GL_COMPUTE_SHADER: ss <<"ComputeShader"; break;
		case GL_SHADER: ss <<"ComputeShader"; break;
		default: ss <<"Unsupported shader type";
	}
	ss << ": Compilation failed";
	if (infoLogLength > 0) {
		infoLog = new GLchar[infoLogLength];
		glGetShaderInfoLog(shader, infoLogLength, NULL, infoLog);
		ss <<":" << std::endl << infoLog;
		delete infoLog;
	}
	ss << std::endl;
	message = ss.str();
}

ShaderException::ShaderException(const ShaderException& ex) : runtime_error(""), shader(ex.shader), message(message) {
}

ShaderException::~ShaderException() {

}

const char* ShaderException::what() const throw() {
	return message.c_str();
}


