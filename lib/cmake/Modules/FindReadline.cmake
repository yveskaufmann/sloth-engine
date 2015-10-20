# Find READLINE
# Find the native READLINE includes and library.  Once done this will define

#   READLINE_INCLUDE_DIRS   - where to find READLINE.h, etc.
#   READLINE_LIBRARIES      - List of libraries when using READLINE.
#   READLINE_FOUND          - True if READLINE found.
# An includer may set READLINE_ROOT to a READLINE installation root to tell this
# module where to look.

if(NOT READLINE_FOUND)
  find_path(READLINE_INCLUDE_DIR readline/readline.h
    HINTS ${READLINE_ROOT_DIR} PATH_SUFFIXES include)
  find_library(READLINE_LIBRARY readline
    HINTS ${READLINE_ROOT_DIR} PATH_SUFFIXES lib)
# find_library(NCURSES_LIBRARY ncurses)   # readline depends on libncurses
  mark_as_advanced(READLINE_INCLUDE_DIR READLINE_LIBRARY)

  include(FindPackageHandleStandardArgs)
  find_package_handle_standard_args(Readline DEFAULT_MSG
    READLINE_LIBRARY READLINE_INCLUDE_DIR)

  set(READLINE_INCLUDE_DIRS ${READLINE_INCLUDE_DIR})
  set(READLINE_LIBRARIES ${READLINE_LIBRARY} )
endif(NOT READLINE_FOUND)
