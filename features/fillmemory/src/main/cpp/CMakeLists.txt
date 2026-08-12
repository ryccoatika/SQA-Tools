cmake_minimum_required(VERSION 3.30.5)

project("fill-memory")

add_library(
        ${PROJECT_NAME}
        SHARED
        fill-memory.cpp)
