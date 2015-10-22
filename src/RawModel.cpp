//
// Created by fxdapokalypse on 21.10.15.
//
#include "RawModel.h"

RawModel::RawModel(GLuint vaoId, unsigned long countOfVertices) : vaoId(vaoId), countOfVertices(countOfVertices) {
}

RawModel::~RawModel() {
	glDeleteVertexArrays(1, &vaoId);
	glDeleteBuffers(1, &vaoId);
	// glDeleteBuffers(1, &vbo);
}

void RawModel::render() const {
	glBindVertexArray(vaoId);
	glEnableVertexAttribArray(0);
	glDrawArrays(GL_TRIANGLES, 0, getCountOfVertices());
	glDisableVertexAttribArray(0);
	glBindVertexArray(0);
}

RawModel* RawModel::loadFromFloatArray(float *vertices, unsigned long length) {

	GLuint vbo, vao;

	// glGenBuffers(1, &vao);
	glGenBuffers(1, &vbo);

	glGenVertexArrays(1, &vao);
	glBindVertexArray(vao);

	glBindBuffer(GL_ARRAY_BUFFER, vbo);
	glBufferData(GL_ARRAY_BUFFER, length, vertices, GL_STATIC_DRAW);
	glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 3 * sizeof(GLfloat), (GLvoid*)0);

	glBindBuffer(GL_ARRAY_BUFFER, 0);
	glBindVertexArray(0);
	
	return new RawModel(vao, length);
}