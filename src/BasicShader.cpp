//
// Created by fxdapokalypse on 23.10.15.
//

#include "BasicShader.h"

const char *vertexShaderSrc =
	"#version 330 core"
		"\n"
		"layout (location = 0) in vec3 position;"
		"uniform float time;"
		"\n"
		"void main() {"
		"	float xst = 1.5;"
		"   float yst = 0.3;"
		"   mat4 rotate = mat4("
		"   	vec4(cos(time), sin(time), 0.0 , 0.0),"
		"   	vec4(-sin(time), cos(time), 0.0, 0.0),"
		"		vec4(0.0, 0.0, 1.0, 0.0),"
		"		vec4(0.0, 0.0, 0.0, 1.0)"
		"   );"
		"   mat4 transform = mat4("
		"   	vec4(1.0, 0.0, 0.0, xst),"
		"   	vec4(0.0, 1.0, 0.0, yst),"
		"   	vec4(0.0, 0.0, 1.0, 0.0),"
		"   	vec4(0.0, 0.0, 0.0, 1.0)"
		"   );"
		"mat4 view = mat4("
	  	"   	vec4(1.0, 0.0, 0.0, 0.0),"
	  	"   	vec4(0.0, 1.0, 0.0, 0.0),"
	  	"   	vec4(0.0, 0.0, 0.0, 0.0),"
	  	"   	vec4(0.0, 0.0, 0.0, 1.0)"
	  	"   );"
		"   mat4 m =  view * rotate * transform;"
		"	gl_Position = vec4(m * vec4(position, 1.0));"
		"}"
		"\n";

const char *fragmentShaderSrc =
	"#version 330 core"
		"\n"
		"uniform vec3 in_color;"
		"uniform float time;"
		"out vec4 color;"
		"\n"
		"void main() {"
		"	color = vec4( in_color + sin(time) * 0.3, 1.0f);"
		"}";

BasicShader::BasicShader() : ShaderProgram() {}

void BasicShader::init() throw(ShaderException) {
	compileAndLink(vertexShaderSrc, fragmentShaderSrc);
}

void BasicShader::loadAllUniformLocations() {
	colorUniformLocation = getUniformLocation("in_color");
	timeUniformLocation = getUniformLocation("time");
}

void BasicShader::bindAllAttributeLocations() {
	bindAttributeLocation("position", 0);
}

void BasicShader::loadColor(glm::vec3 color) {
	setUniform(colorUniformLocation, color);

}

void BasicShader::updateTimer() {
	setUniformCurrentTime(timeUniformLocation);
}





