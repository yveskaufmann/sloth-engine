//
// Created by fxdapokalypse on 21.10.15.
//

#ifndef GL_TUT_DISPLAY_H
#define GL_TUT_DISPLAY_H

#include <string>

#include <DisplayException.h>
#include <gl_stuff.h>

class Display {
public:

	Display(GLFWwindow* window, std::string title);
	~Display();

	/**
	 * Checks if the windows was requested to closed by the user
	 * @return true if the user requested to close the display.
	 */
	bool shouldClose() const;

	/**
	 * Update the underlying display which
	 * swap the buffers and so on.
	 */
	void update();

	/**
	 * Returns the pointer to the  glfw window.
	 */
	GLFWwindow* getWindow() const;

	/**
	 * Set the title of the display
	 */
	Display& setTitle(std::string title);

	/**
	 * Set the width of the display
	 */
	Display& setWidth(int width);

	/**
	 * Set the width of the display
	 */
	Display& setHeight(int height);

	/**
	 * Returns the width in pixels of the display
	 */
	int getWidth();

	/**
	 * Returns the height of pixels of the display
	 */
	int getHeight();

	/**
	 * Set the the with and height of the display in pixels
	 */
	Display& setSize(int width, int height);

	/**
	 * Returns the current title of this display
	 */
	std::string getTitle();

	/**
	 * Enable this display and assign this display as open gl rendering
	 * context.
	 */
	void enable();

	/**
	 * Disable this display and detach this display as open gl rendering
	 * context.
	 */
	void disable();

private:
	/**
	 * The display implementation pointer
	 */
	GLFWwindow* window;
	std::string title;
};


#endif //GL_TUT_DISPLAY_H
