cmake_minimum_required(VERSION 3.1)

project(hello)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++1y -Wall -Wextra")

add_executable(A 1.cpp)
add_executable(B 2.cpp)
