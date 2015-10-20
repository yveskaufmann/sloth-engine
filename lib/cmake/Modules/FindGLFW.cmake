# Find GLFW
# Find the native GLFW includes and library.  Once done this will define

#   GLFW_INCLUDE_DIRS   - where to find GLFW.h, etc.
#   GLFW_LIBRARIES      - List of libraries when using GLFW.
#   GLFW_FOUND          - True if GLFW found.
# An includer may set GLFW_ROOT to a GLFW installation root to tell this
# module where to look.

if(NOT EXISTS ${CMAKE_SOURCE_DIR}/lib/glfw )
    find_package(Git)
    if(NOT GIT_FOUND)
        message(FATAL_ERROR "Git is required please install git and ensure that the git executable is at your path.")
    endif()
    execute_process(
        COMMAND ${GIT_EXECUTABLE} submodule update --init
    )
endif()

if(NOT GLFW_FOUND)
  find_path(GLFW_INCLUDE_DIR GLFW/glfw3.h
    HINTS ${GLFW_ROOT_DIR} PATH_SUFFIXES include)
  find_library(GLFW_LIBRARY glfw
    HINTS ${GLFW_ROOT_DIR} PATH_SUFFIXES lib lib/x86_64-linux-gnu)

  mark_as_advanced(GLFW_INCLUDE_DIR GLFW_LIBRARY)
  include(FindPackageHandleStandardArgs)
  find_package_handle_standard_args(GLFW DEFAULT_MSG
    GLFW_LIBRARY GLFW_INCLUDE_DIR)

  set(GLFW_INCLUDE_DIRS ${GLFW_INCLUDE_DIR})
  set(GLFW_LIBRARIES ${GLFW_LIBRARY} )
endif(NOT GLFW_FOUND)
