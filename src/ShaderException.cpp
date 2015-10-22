//
// Created by fxdapokalypse on 22.10.15.
//

#include "ShaderException.h"

#include <sstream>

ShaderException::ShaderException(GLuint shader) : runtime_error(""), shader(shader) {
}
ShaderException::~ShaderException() { }
const char* ShaderException::what() const throw() {
	std::stringstream ss;
	GLchar *infoLog = NULL;
	GLint shaderType;
	GLint infoLogLength;

	glGetShaderiv(shader, GL_SHADER_TYPE, &(shaderType));
	glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLogLength);

	ss << "Error::Shader::";
	switch (shaderType) {
		case GL_VERTEX_SHADER: ss << "VertexShader"; break;
		case GL_FRAGMENT_SHADER: ss << "FragmentShader";break;
		case GL_GEOMETRY_SHADER: ss << "GeometryShader"; break;
		case GL_COMPUTE_SHADER: ss << "ComputeShader"; break;
		case GL_SHADER: ss << "ComputeShader"; break;
		default: ss << "Unsupported shader type";
	}
	ss << ":" << " Compilation failed";
	if (infoLogLength > 0) {
		infoLog = new GLchar[infoLogLength];
		glGetShaderInfoLog(shader, sizeof(infoLogLength), NULL, infoLog);
		ss << ":" << std::endl;
		ss << infoLog;
		delete infoLog;
	}

	ss << std::endl;

	return ss.str().c_str();
}