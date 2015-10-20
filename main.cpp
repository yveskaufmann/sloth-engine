#include <iostream>
#include <unistd.h>
#include <math.h>
#include <stdlib.h>
#include <time.h>

#ifdef USE_GLEW
	#include <GL/glew.h>
#else 
	#define GL_GLEXT_PROTOTYPES
	#include <GL/gl.h>
	#include <GL/glext.h>
#endif
#include <GLFW/glfw3.h>

void key_callback(GLFWwindow *window, int key, int scanCode, int action, int mode);
GLuint compileShader(GLenum shaderType, const char* shaderSource);

int main() {	
	int width = 800;
	int height = 600;
	const char *vertexShaderSrc = 
		"#version 330 core"
		"\n"
		"layout (location = 0) in vec3 position;"
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
	
	GLfloat vertices[] = {
		  0.0f, -0.5f, 0.0f,
		 -0.5f, -0.5f, 0.0f,
		 -0.25f, 0.5,  0.0f,
		  0.25f, 0.5f, 0.0f,
		  0.5f, -0.5,  0.0f,
	};
	
	GLuint indices[] = { 
		0, 1, 2,
		0, 3, 4
	};
	
	glfwInit();
	glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
	glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
	glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
	glfwWindowHint(GLFW_RESIZABLE, false);

	GLFWwindow *window = glfwCreateWindow(width, height, "GL Tutorial", nullptr, nullptr);
	if (window == nullptr) {
		std::cout << "Failed to create a window." << std::endl;
		glfwTerminate();
		return -1;
	}
	glfwMakeContextCurrent(window);
	glfwSetKeyCallback(window, key_callback);

	glewExperimental = true;
	if (glewInit() != GLEW_OK) {
		std::cout << "Failed to initialize glew." << std::endl;
		return -1;
	}
	
	GLuint vertexShader = compileShader(GL_VERTEX_SHADER, vertexShaderSrc);
	GLuint fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentShaderSrc);


	if (vertexShader == -1 || fragmentShader == -1 ) {
		std::cout << "Failed to compile the required shaders" << std::endl;
		glfwTerminate();
		return -1;
	}

	GLuint shaderProgram = glCreateProgram();
	glAttachShader(shaderProgram, vertexShader);
	glAttachShader(shaderProgram, fragmentShader);
	glLinkProgram(shaderProgram);

	GLint linkStatus;
	glGetProgramiv(shaderProgram, GL_LINK_STATUS, &linkStatus);
	if (! linkStatus) {
		GLchar infoLog[255];
		glGetProgramInfoLog(shaderProgram, sizeof(infoLog), NULL, infoLog);
		std::cout << "Shader Link Error: " << infoLog << std::endl;
		glfwTerminate();
		return -1;
	}

	glDeleteShader(vertexShader);
	glDeleteShader(fragmentShader);

	/**
	 * Generates a buffer for the verticies.
	 */
	GLuint vbo;
	glGenBuffers(1, &vbo);
	
	GLuint vao;
	glGenVertexArrays(1, &vao);
	
	GLuint ebo;
	glGenBuffers(1, &ebo);

	glBindVertexArray(vao);
	glBindBuffer(GL_ARRAY_BUFFER, vbo);
	glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);
	
	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
	glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices, GL_STATIC_DRAW);
	
	glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 3 * sizeof(GLfloat), (GLvoid*)0);
	glEnableVertexAttribArray(0);
	
	glBindBuffer(GL_ARRAY_BUFFER, 0);
	glBindVertexArray(0);

	glViewport(0, 0, width, height);
	
	double  previousTime = 0;
	while (! glfwWindowShouldClose(window)) {
		double currentTime = glfwGetTime();
		double diffTime = currentTime - previousTime;
		previousTime = currentTime;
			
		glfwPollEvents();	
		GLint colorLocation = glGetUniformLocation(shaderProgram, "in_color");
		glUniform3f(colorLocation, sin(currentTime) * 1.0f, 0.4f * cos(currentTime * 2.0), 0.2f);
		glUseProgram(shaderProgram);
		
		glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT);
		glBindVertexArray(vao);
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);

		glfwSwapBuffers(window);
	}
	
	glDeleteVertexArrays(1, &vao);
	glDeleteBuffers(1, &ebo);
	glDeleteBuffers(1, &vbo);
	
	glfwTerminate();
	
	return 0;
}

void key_callback(GLFWwindow *window, int key, int scanCode, int action, int mode) {
	if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
		glfwSetWindowShouldClose(window, GL_TRUE);
	}
}


GLuint compileShader(GLenum shaderType, const char* shaderSource) {
	
	GLint success;
	GLchar infoLog[512];
	GLuint shader = glCreateShader(shaderType);
	
	glShaderSource(shader, 1, &shaderSource, NULL);
	glCompileShader(shader);
	glGetShaderiv(shader, GL_COMPILE_STATUS, &success);

	if (! success) {
		glGetShaderInfoLog(shader, sizeof(infoLog), NULL, infoLog);
		std::string shaderTypeStr = (shaderType == GL_VERTEX_SHADER) ? "VertexShader" : "FragmentShader";
		std::cout << "Error::Shader::" << shaderTypeStr << " Compilation failed:"  << infoLog << std::endl;
		return -1;
	}	

	return shader;
}
