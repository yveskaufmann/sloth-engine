//
// Created by fxdapokalypse on 22.10.15.
//

#ifndef GL_TUT_SHADERPROGRAM_H
#define GL_TUT_SHADERPROGRAM_H

#include <gl_stuff.h>
#include <ICleanable.h>
#include <ShaderException.h>

class ShaderProgram : ICleanable {
public:

	ShaderProgram(const char *vertexShaderSrc, const char *fragmentShaderSrc) throw(ShaderException);
	~ShaderProgram();

	void start();
	void stop();

	void clean();

private:
	GLuint compileAndLink(const char *vertexShaderSrc, const char *fragmentShaderSrc) throw(ShaderException);
	GLuint compileShader(GLenum shaderType, const char* shaderSource) throw(ShaderException) ;

	GLuint shaderProgram;
	GLuint vertexShader;
	GLuint fragmentShader;
};


#endif //GL_TUT_SHADERPROGRAM_H
