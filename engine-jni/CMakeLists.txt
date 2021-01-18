cmake_minimum_required(VERSION 3.4.1)

add_library(engine-jni
            SHARED
            src/main/cpp/libengine-jni.cpp
            ../cpp_shared/src/Utils.cpp
            ../cpp_shared/src/Engine.cpp)

include_directories(../cpp_shared/include)

target_link_libraries(engine-jni)