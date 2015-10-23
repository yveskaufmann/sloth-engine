//
// Created by fxdapokalypse on 22.10.15.
//

#ifndef GL_TUT_SHADERPROGRAM_H
#define GL_TUT_SHADERPROGRAM_H

#include <gl_stuff.h>
#include <ICleanable.h>
#include <ShaderException.h>
#include <glm/glm.hpp>

class ShaderProgram : ICleanable {
public:
	ShaderProgram();
	~ShaderProgram();
	virtual void init() throw(ShaderException) = 0;
	void start();
	void stop();
	void clean();

protected:
	GLuint compileAndLink(const char *vertexShaderSrc, const char *fragmentShaderSrc) throw(ShaderException);
	void bindAttributeLocation(const char *attributeName, GLuint location);
	GLuint getUniformLocation(const char *name);


	virtual void bindAllAttributeLocations() = 0;
	virtual void loadAllUniformLocations() = 0;

	ShaderProgram& setUniform(GLuint location, GLfloat value);
	ShaderProgram& setUniform(GLuint location, GLdouble value);
	ShaderProgram& setUniform(GLuint location, GLint value);
	ShaderProgram& setUniform(GLuint location, GLuint value);

	ShaderProgram& setUniform(GLuint location, glm::vec2 vector);
	ShaderProgram& setUniform(GLuint location, glm::vec3 vector);
	ShaderProgram& setUniform(GLuint location, glm::vec4 vector);
	ShaderProgram& setUniform(GLuint location, glm::mat4 matrix);
	ShaderProgram& setUniformCurrentTime(GLuint location);

private:
	GLuint compileShader(GLenum shaderType, const char* shaderSource) throw(ShaderException);

	GLuint shaderProgram;
	GLuint vertexShader;
	GLuint fragmentShader;
};


#endif //GL_TUT_SHADERPROGRAM_H
