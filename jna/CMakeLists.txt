cmake_minimum_required(VERSION 3.4.1)

add_library(measurement-jna
            SHARED
            src/main/cpp/libmeasurement-jna.cpp
            src/main/cpp/Utils.cpp)