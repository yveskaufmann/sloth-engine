//
// Created by fxdapokalypse on 23.10.15.
//

#ifndef GL_TUT_BASICSHADER_H
#define GL_TUT_BASICSHADER_H

#include <ShaderProgram.h>

class BasicShader : public ShaderProgram {
public:
	BasicShader();
	void loadColor(glm::vec3 color);
	void updateTimer();
	void init() throw(ShaderException) override;

protected:
	void bindAllAttributeLocations() override;
	void loadAllUniformLocations() override;
private:
	GLuint colorUniformLocation;
	GLuint timeUniformLocation;
};

#endif //GL_TUT_BASICSHADER_H
