cmake_minimum_required(VERSION 3.30.5)

project("dev-info")

add_library(
        ${PROJECT_NAME}
        SHARED
        dev-info.cpp)
