cmake_minimum_required(VERSION 3.1)

project(hello)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++1y -Wall -Wextra")

add_executable(1 1.cpp)
add_executable(4 4.cpp)
add_executable(5 5.cpp)
add_executable(6 6.cpp)
