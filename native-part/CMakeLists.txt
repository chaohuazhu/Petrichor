cmake_minimum_required( VERSION 3.8 )
project( FaceDetection )

set( CMAKE_CXX_STANDARD 11 )

set( CMAKE_SYSTEM_NAME Android )

include_directories( lib/FaceDetection/include )
include_directories( include )

set( SOURCES
        include/detector-lib.h
        src/detector-lib.cpp )

add_subdirectory( lib/FaceDetection )
add_library( detector-lib SHARED ${SOURCES} )

target_link_libraries( detector-lib seeta_facedct_lib )
