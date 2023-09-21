cmake_minimum_required(VERSION 3.1)

project(hello)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++1y -Wall -Wextra")

add_executable(1 fraction.cpp fraction_main.cpp)
add_executable(2 set.cpp set_main.cpp)
