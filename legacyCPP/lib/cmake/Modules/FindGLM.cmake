# FindGLM - attempts to locate the glm matrix/vector library.
#
# This module defines the following variables (on success):
#   GLM_INCLUDE_DIRS  - where to find glm/glm.hpp
#   GLM_FOUND         - if the library was successfully located
#

# default search dirs
find_package(Git)

if(NOT GIT_FOUND)
	message(FATAL_ERROR "Git is required please install git and ensure that the git executable is at your path.")
endif()

execute_process(
	COMMAND ${GIT_EXECUTABLE} submodule update --init
)

SET(_glm_HEADER_SEARCH_DIRS
	"${CMAKE_SOURCE_DIR}/lib/glm"
	 "/usr/include"
	 "/usr/local/include"
)

# locate header
FIND_PATH(GLM_INCLUDE_DIR "glm/glm.hpp"
	PATHS ${CMAKE_SOURCE_DIR}/lib/glm ${_glm_HEADER_SEARCH_DIRS} NO_DEFAULT_PATH)

INCLUDE(FindPackageHandleStandardArgs)
FIND_PACKAGE_HANDLE_STANDARD_ARGS(GLM DEFAULT_MSG GLM_INCLUDE_DIR)
mark_as_advanced(_glm_HEADER_SEARCH_DIRS)

IF(GLM_FOUND)
    SET(GLM_INCLUDE_DIRS "${GLM_INCLUDE_DIR}")
ENDIF()
