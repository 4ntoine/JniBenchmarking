cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

# https://github.com/abseil/abseil-cpp/issues/626
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -DABSL_NO_XRAY_ATTRIBUTES=1")

set(librpc_PROTOBUF_PROTOC_EXECUTABLE "/usr/local/bin/protoc" CACHE STRING "Protoc binary on host")
set(librpc_GRPC_CPP_PLUGIN_EXECUTABLE "/usr/local/bin/grpc_cpp_plugin" CACHE STRING "gRPC CPP plugin binary on host")

set(GRPC_SRC_DIR libs/grpc)

set(GRPC_BUILD_DIR build/grpc/outputs/${ANDROID_ABI})
file(MAKE_DIRECTORY ${GRPC_BUILD_DIR})

add_subdirectory(${GRPC_SRC_DIR} ${GRPC_BUILD_DIR})

set(GRPC_PROTO_GENS_DIR ${CMAKE_BINARY_DIR}/generated)
file(MAKE_DIRECTORY ${GRPC_PROTO_GENS_DIR})
include_directories(${GRPC_PROTO_GENS_DIR})

function(android_protobuf_grpc_generate_cpp SRC_FILES HDR_FILES INCLUDE_ROOT)
    if(NOT ARGN)
        message(SEND_ERROR "Error: android_protobuf_grpc_generate_cpp() called without any proto files")
        return()
    endif()

    set(${SRC_FILES})
    set(${HDR_FILES})
    set(PROTOBUF_INCLUDE_PATH -I ${INCLUDE_ROOT})

    foreach(FIL ${ARGN})
        get_filename_component(ABS_FIL ${FIL} ABSOLUTE)
        get_filename_component(FIL_WE ${FIL} NAME_WE)
        file(RELATIVE_PATH REL_FIL ${CMAKE_CURRENT_SOURCE_DIR}/${INCLUDE_ROOT} ${ABS_FIL})
        get_filename_component(REL_DIR ${REL_FIL} DIRECTORY)
        set(RELFIL_WE "${REL_DIR}/${FIL_WE}")

        list(APPEND ${SRC_FILES} "${GRPC_PROTO_GENS_DIR}/${RELFIL_WE}.pb.cc")
        list(APPEND ${HDR_FILES} "${GRPC_PROTO_GENS_DIR}/${RELFIL_WE}.pb.h")
        list(APPEND ${SRC_FILES} "${GRPC_PROTO_GENS_DIR}/${RELFIL_WE}.grpc.pb.cc")
        list(APPEND ${HDR_FILES} "${GRPC_PROTO_GENS_DIR}/${RELFIL_WE}.grpc.pb.h")

        # Anton Smirnov: for some reason using ${REL_FIL} fails with "file not found",
        # so using relative to include root path instead.
        add_custom_command(
                OUTPUT "${GRPC_PROTO_GENS_DIR}/${RELFIL_WE}.grpc.pb.cc"
                "${GRPC_PROTO_GENS_DIR}/${RELFIL_WE}.grpc.pb.h"
                "${GRPC_PROTO_GENS_DIR}/${RELFIL_WE}.pb.cc"
                "${GRPC_PROTO_GENS_DIR}/${RELFIL_WE}.pb.h"
                COMMAND ${librpc_PROTOBUF_PROTOC_EXECUTABLE}
                ARGS --grpc_out=${GRPC_PROTO_GENS_DIR}
                --cpp_out=${GRPC_PROTO_GENS_DIR}
                --plugin=protoc-gen-grpc=${librpc_GRPC_CPP_PLUGIN_EXECUTABLE}
                ${PROTOBUF_INCLUDE_PATH}
                ${INCLUDE_ROOT}/${REL_FIL}
                WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
                DEPENDS ${librpc_PROTOBUF_PROTOC_EXECUTABLE} ${librpc_GRPC_CPP_PLUGIN_EXECUTABLE} ${ABS_FIL} )
    endforeach()

    set_source_files_properties(${${SRC_FILES}} ${${HDR_FILES}} PROPERTIES GENERATED TRUE)
    set(${SRC_FILES} ${${SRC_FILES}} PARENT_SCOPE)
    set(${HDR_FILES} ${${HDR_FILES}} PARENT_SCOPE)
endfunction()

set(PROTO_BASE_DIR src/main/proto_lite)

android_protobuf_grpc_generate_cpp(
        HELLOWORLD_PROTO_SRCS HELLOWORLD_PROTO_HDRS ${PROTO_BASE_DIR} ${PROTO_BASE_DIR}/matches.proto)

add_library(rpc_proto_lib
        SHARED ${HELLOWORLD_PROTO_HDRS} ${HELLOWORLD_PROTO_SRCS})

target_link_libraries(rpc_proto_lib
        grpc++
        libprotobuf
        android
        log)

find_library(log-lib
        log)

add_library(rpc
        SHARED src/main/cpp/librpc.cpp)

target_include_directories(rpc
        PRIVATE ${HELLOWORLD_PROTO_HEADERS})

target_link_libraries(rpc
        rpc_proto_lib
        android
        ${log-lib})
