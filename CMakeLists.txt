cmake_minimum_required(VERSION 3.0)
project(GL_TUT)

enable_language(C)
enable_language(CXX)
set(CMAKE_EXPORT_COMPILE_COMMANDS, 1)
set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} "${CMAKE_SOURCE_DIR}/lib/cmake/Modules/")
set(SOURCE_FILES 
	${CMAKE_SOURCE_DIR}/main.cpp
)

if (UNIX) 
	set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
endif ()
	
if ( MSVC )
	foreach( OUTPUTCONFIG ${CMAKE_CONFIGURATION_TYPES} )
		string( TOUPPER ${OUTPUTCONFIG} OUTPUTCONFIG )
		set( CMAKE_RUNTIME_OUTPUT_DIRECTORY_${OUTPUTCONFIG} ${CMAKE_CURRENT_BINARY_DIR}/dist )
		set( CMAKE_LIBRARY_OUTPUT_DIRECTORY_${OUTPUTCONFIG} ${CMAKE_CURRENT_BINARY_DIR}/dist/lib )
		set( CMAKE_ARCHIVE_OUTPUT_DIRECTORY_${OUTPUTCONFIG} ${CMAKE_CURRENT_BINARY_DIR}/dist/lib )
	endforeach( OUTPUTCONFIG CMAKE_CONFIGURATION_TYPES )
else ()	
	set( CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/dist )
	set( CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/dist/lib )
	set( CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/dist/lib )
endif ( MSVC )
	
file(COPY models textures shaders DESTINATION ${CMAKE_CURRENT_BINARY_DIR}/dist)


if(NOT EXISTS ${CMAKE_SOURCE_DIR}/lib/glfw )
    find_package(Git)
    if(NOT GIT_FOUND)
        message(FATAL_ERROR "Git is required please install git and ensure that the git executable is at your path.")
    endif()
    execute_process(
        COMMAND ${GIT_EXECUTABLE} submodule update --init
    )
endif()

### Include GLFW Library ###
add_subdirectory(${CMAKE_SOURCE_DIR}/lib/glfw)
include_directories(${CMAKE_SOURCE_DIR}/lib/glfw/include)

### Include GLM Library ###
find_package(GLM REQUIRED)
include_directories(${GLM_INCLUDE_DIRS})
link_directories(${GLM_LIBRARY_DIRS})
add_definitions(${GLM_DEFINITIONS})

### Include GLEW Library ###
add_definitions(-DUSE_GLEW)
add_definitions(-DGLEW_STATIC)
find_package(GLEW REQUIRED)
include_directories(${GLEW_INCLUDE_DIRS})
link_directories(${GLEW_LIBRARIES})

if(NOT GLEW_FOUND)
	message(Error 'GLEW not found')
endif(NOT GLEW_FOUND)

add_executable(GL_TUT ${SOURCE_FILES})
target_link_libraries(GL_TUT glfw ${GLFW_LIBRARIES} ${GLEW_LIBRARIES} )

add_custom_target(run 
	COMMAND GL_TUT
	WORKING_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}/dist
)


