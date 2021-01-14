cmake_minimum_required(VERSION 3.4.1)

# flatbuffer CMakeLists.txt
# (https://github.com/google/flatbuffers/blob/master/android/app/src/main/cpp/flatbuffers/CMakeLists.txt)

set(FLATBUFFERS_SRC libs/flatbuffers)
include_directories(${FLATBUFFERS_SRC}/include)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11 -fexceptions -Wall -DFLATBUFFERS_TRACK_VERIFIER_BUFFER_SIZE")

# Certain platforms such as ARM do not use signed chars by default
# which causes issues with certain bounds checks.
set(CMAKE_CXX_FLAGS
        "${CMAKE_CXX_FLAGS} -fsigned-char")

set(FlatBuffers_Library_SRCS
        ${FLATBUFFERS_SRC}/include/flatbuffers/base.h
        ${FLATBUFFERS_SRC}/include/flatbuffers/flatbuffers.h
        ${FLATBUFFERS_SRC}/include/flatbuffers/hash.h
        ${FLATBUFFERS_SRC}/include/flatbuffers/idl.h
        ${FLATBUFFERS_SRC}/include/flatbuffers/util.h
        ${FLATBUFFERS_SRC}/include/flatbuffers/reflection.h
        ${FLATBUFFERS_SRC}/include/flatbuffers/reflection_generated.h
        ${FLATBUFFERS_SRC}/include/flatbuffers/stl_emulation.h
        ${FLATBUFFERS_SRC}/include/flatbuffers/flexbuffers.h
        ${FLATBUFFERS_SRC}/include/flatbuffers/registry.h
        ${FLATBUFFERS_SRC}/include/flatbuffers/minireflect.h
        ${FLATBUFFERS_SRC}/src/idl_parser.cpp
        ${FLATBUFFERS_SRC}/src/idl_gen_text.cpp
        ${FLATBUFFERS_SRC}/src/reflection.cpp
        ${FLATBUFFERS_SRC}/src/util.cpp
        ${FLATBUFFERS_SRC}/src/idl_gen_fbs.cpp
        ${FLATBUFFERS_SRC}/src/code_generators.cpp
        )

set(FlatBuffers_Test_SRCS
        ${FLATBUFFERS_SRC}/tests/test.cpp
        ${FLATBUFFERS_SRC}/tests/test_assert.h
        ${FLATBUFFERS_SRC}/tests/test_builder.h
        ${FLATBUFFERS_SRC}/tests/test_assert.cpp
        ${FLATBUFFERS_SRC}/tests/test_builder.cpp
        ${FLATBUFFERS_SRC}/tests/native_type_test_impl.h
        ${FLATBUFFERS_SRC}/tests/native_type_test_impl.cpp
        )

add_library( # Sets the name of the library.
        flatbuffers
        SHARED

        ${FlatBuffers_Library_SRCS}
#        ${FlatBuffers_Test_SRCS}
        ${Generated_SRCS}
        )

#add_library( # Sets the name of the library.
#        flatbuffers_tests
#
#        ${FlatBuffers_Test_SRCS}
#        )

# ---

# generated messages path
string(TOLOWER ${CMAKE_BUILD_TYPE} BUILD_TYPE_DIR)
set( MESSAGES_GEN_DIR build/generated/source/flatbuffers/cpp )

# Sets lib_src_DIR to the path of the target CMake project.
set( FB_SRC_DIR libs/flatbuffers )

# Sets lib_build_DIR to the path of the desired output directory.
set( FB_BUILD_DIR build/generated/source/flatbuffers/build )
file(MAKE_DIRECTORY ${FB_BUILD_DIR})

# Adds the CMakeLists.txt file located in the specified directory
# as a build dependency.
#add_subdirectory( # Specifies the directory of the CMakeLists.txt file.
#        ${FB_SRC_DIR}
#
#        # Specifies the directory for the build outputs.
#        ${FB_BUILD_DIR} )

# Adds the output of the additional CMake build as a prebuilt static
# library and names it libflatbuffers.
#include_directories( ${FB_SRC_DIR}/../src )

include_directories( ${MESSAGES_GEN_DIR} )

add_library(engine-fb
            SHARED
            src/main/cpp/libengine.cpp
            src/main/cpp/Utils.cpp
            src/main/cpp/Engine.cpp
            build/generated/source/flatbuffers/cpp/matches_common_generated.h
            build/generated/source/flatbuffers/cpp/matches_request_generated.h
            build/generated/source/flatbuffers/cpp/matches_response_generated.h)

# Links the top-level CMake build output against libprotobuf.
target_link_libraries( engine-fb flatbuffers )