//
// Created by fxdapokalypse on 21.10.15.
//

#ifndef GL_TUT_DISPLAYMANAGER_H
#define GL_TUT_DISPLAYMANAGER_H

#include <string>
#include <map>

#include <gl_stuff.h>
#include <Display.h>
#include <DisplayException.h>
#include <ICleanable.h>

class DisplayManager : ICleanable {
public:
	DisplayManager();
	~DisplayManager();

	DisplayManager& setTitle(std::string title);
	DisplayManager& setWidth(int width);
	DisplayManager& setHeight(int height);
	DisplayManager& setGLContextVersion(int major, int minor);

	/**
	 * Window related hints
	 */
	DisplayManager& setResizeable(bool resizeable);
	DisplayManager& setVisible(bool visible);
	DisplayManager& setFocused(bool focused);
	DisplayManager& enableWindowAutoIconify(bool enabled);
	DisplayManager& enableWindowFloating(bool enable);

	DisplayManager& setWindowHint(int hint, int value);
	DisplayManager& toggleWindowHint(int hint, bool value);
	DisplayManager& reset();

	Display& build() throw(DisplayException);

	void clean();
private:
	Display* display;
	std::string title;
	int width;
	int height;
	GLFWwindow *window;
	std::map<int, int> windowHints;
	bool alreadyCleaned = false;
};

#endif //GL_TUT_DISPLAYMANAGER_H
