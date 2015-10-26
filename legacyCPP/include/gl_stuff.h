#ifdef USE_GLEW
#include <GL/glew.h>
#else
	/**
	 * This case is used only inside a ide in order to achieve
	  * a proper autocompletion.
 	 */
	#define GL_GLEXT_PROTOTYPES
	#include <GL/gl.h>
	#include <GL/glext.h>
#endif
#include <GLFW/glfw3.h>


