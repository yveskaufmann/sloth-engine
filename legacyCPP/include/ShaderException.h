//
// Created by fxdapokalypse on 22.10.15.
//

#ifndef GL_TUT_SHADEREXCEPTION_H
#define GL_TUT_SHADEREXCEPTION_H

#include <exception>
#include <stdexcept>
#include <string>
#include <gl_stuff.h>


class ShaderException : public std::runtime_error {
public:
	ShaderException(GLuint shader);
	ShaderException(std::string message);
	ShaderException(const ShaderException& ex);
	~ShaderException();
	virtual const char* what() const throw();
private:
	GLuint shader;
	std::string message;
};


#endif //GL_TUT_SHADEREXCEPTION_H
