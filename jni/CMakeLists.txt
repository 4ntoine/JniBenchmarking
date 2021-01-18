cmake_minimum_required(VERSION 3.4.1)

add_library(measurement-jni
            SHARED
            src/main/cpp/libmeasurement-jni.cpp
            ../cpp_shared/src/Utils.cpp)

include_directories(../cpp_shared/include)