//
// Created by fxdapokalypse on 22.10.15.
//

#include "DisplayException.h"

DisplayException::DisplayException(std::string message) : runtime_error(message) {
}
DisplayException::~DisplayException() {}