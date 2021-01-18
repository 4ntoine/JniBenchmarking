cmake_minimum_required(VERSION 3.4.1)

add_library(measurement-jna
            SHARED
            src/main/cpp/libmeasurement-jna.cpp
            ../cpp_shared/src/Utils.cpp)

include_directories(../cpp_shared/include)

target_link_libraries(measurement-jna)