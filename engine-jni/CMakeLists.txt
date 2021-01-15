cmake_minimum_required(VERSION 3.4.1)

add_library(engine-jni
            SHARED
            src/main/cpp/libengine-jni.cpp
            src/main/cpp/Utils.cpp
            src/main/cpp/Engine.cpp)

target_link_libraries(engine-jni)