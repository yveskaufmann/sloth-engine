//
// Created by fxdapokalypse on 22.10.15.
//

#ifndef GL_TUT_DISPLAYEXCEPTION_H
#define GL_TUT_DISPLAYEXCEPTION_H

#include <exception>
#include <stdexcept>
#include <string>

class DisplayException : public std::runtime_error {
public:
	DisplayException(std::string message);
	~DisplayException();
};


#endif //GL_TUT_DISPLAYEXCEPTION_H
