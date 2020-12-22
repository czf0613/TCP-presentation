cmake_minimum_required(VERSION 2.8.4)
project(socket_io)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

add_executable(socket_io main.cpp)