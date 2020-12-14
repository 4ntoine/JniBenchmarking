cmake_minimum_required(VERSION 3.4.1)

# generated messages path
set( MESSAGES_GEN_DIR build/generated/source/proto/debug/cpp )

# Sets lib_src_DIR to the path of the target CMake project.
set( PROTOBUF_SRC_DIR libs/protobuf/cmake )

# Sets lib_build_DIR to the path of the desired output directory.
set( PROTOBUF_BUILD_DIR build/generated/source/protobuf )
file(MAKE_DIRECTORY ${PROTOBUF_BUILD_DIR})

# Adds the CMakeLists.txt file located in the specified directory
# as a build dependency.
add_subdirectory( # Specifies the directory of the CMakeLists.txt file.
        ${PROTOBUF_SRC_DIR}

        # Specifies the directory for the build outputs.
        ${PROTOBUF_BUILD_DIR} )

# Adds the output of the additional CMake build as a prebuilt static
# library and names it lib_gmath.
include_directories( ${PROTOBUF_SRC_DIR}/../src )

include_directories( ${MESSAGES_GEN_DIR} )

add_library(engine
            SHARED
            src/main/cpp/libengine.cpp
            src/main/cpp/Utils.cpp
            src/main/cpp/Engine.cpp
            build/generated/source/proto/debug/cpp/matches.pb.cc)

# Links the top-level CMake build output against lib_gmath.
target_link_libraries( engine libprotobuf )