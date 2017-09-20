cmake_minimum_required(VERSION 3.4.1)

set( JNI_DIR src/main/jni )
set( SOURCES ${JNI_DIR}/seeta-lib.cpp )

include_directories( ${JNI_DIR}/lib/seeta/include )

add_subdirectory( ${JNI_DIR}/lib/seeta )
add_library( seeta-lib SHARED ${SOURCES} )

target_link_libraries( seeta-lib seeta_facedct_lib )
