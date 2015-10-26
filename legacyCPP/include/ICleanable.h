

#ifndef GL_TUT_ICLOSEABLE_H
#define GL_TUT_ICLOSEABLE_H

/**
 * This interface must be implemented by all
 * classes which have to clean up some resources.
 */
class ICleanable {
public:
	virtual ~ICleanable() { }

	/**
	 * Clean up all no longer needed resources.
	 */
	virtual void clean() = 0;
};

#endif //GL_TUT_ICLOSEABLE_H
