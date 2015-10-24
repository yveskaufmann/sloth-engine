//
// Created by fxdapokalypse on 21.10.15.
//

#ifndef GL_TUT_RAWMODEL_H
#define GL_TUT_RAWMODEL_H

#include <gl_stuff.h>
#include <string>

class RawModel {
public:

	static RawModel *loadFromFile(std::string filename);
	static RawModel *loadFromFloatArray(float* vertices, unsigned long length);

	RawModel(GLuint vaoId, unsigned long countOfVertices);
	~RawModel();

	void render() const;

	GLuint getVAOId() const {
		return vaoId;
	}

	unsigned long getCountOfVertices() const {
		return countOfVertices;
	}

private:
	GLuint vaoId;
	unsigned long countOfVertices;
};


#endif //GL_TUT_RAWMODEL_H
