cmake_minimum_required(VERSION 3.21)

project(vslc VERSION 1.0 LANGUAGES C)

set(VSLC_SOURCES "src/vslc.c"
                 "src/tree.c"
                 "src/graphviz_output.c"
                 "src/symbols.c"
                 "src/symbol_table.c")

set(VSLC_LEXER_SOURCE "src/scanner.l")
set(VSLC_PARSER_SOURCE "src/parser.y")


# === Setup generation of parser and scanner .c files and support headers
find_package(FLEX 2.6 REQUIRED)
find_package(BISON 3.5 REQUIRED)


if(BISON_VERSION VERSION_GREATER_EQUAL 3.8)
  set(BISON_FLAGS -Wcounterexamples)
endif()

set(GEN_DIR "${CMAKE_CURRENT_BINARY_DIR}")
set(SCANNER_GEN_C "${GEN_DIR}/scanner.c")
set(PARSER_GEN_C "${GEN_DIR}/parser.c")
set(CMAKE_EXPORT_COMPILE_COMMANDS true)

flex_target(scanner "${VSLC_LEXER_SOURCE}" "${SCANNER_GEN_C}" DEFINES_FILE "${GEN_DIR}/scanner.h")
bison_target(parser "${VSLC_PARSER_SOURCE}" "${PARSER_GEN_C}" DEFINES_FILE "${GEN_DIR}/parser.h"
                    COMPILE_FLAGS ${BISON_FLAGS})
add_flex_bison_dependency(scanner parser)


# === Finally declare the compiler target, depending on all .c files in the project ===
add_executable(vslc "${VSLC_SOURCES}" "${SCANNER_GEN_C}" "${PARSER_GEN_C}")
# Set some flags specifically for flex/bison
target_include_directories(vslc PRIVATE "include" "${GEN_DIR}")
target_compile_definitions(vslc PRIVATE "YYSTYPE=node_t *")

# Set general compiler flags, such as getting strdup from posix
# -O2 -g
set(CMAKE_BUILD_TYPE RelWithDebInfo)
#set(CMAKE_BUILD_TYPE Debug)
# -std=c17
set_property(TARGET vslc PROPERTY C_STANDARD 17)
target_compile_definitions(vslc PUBLIC _POSIX_C_SOURCE=200809L)
if (MSVC)
    # warning level 4
    target_compile_options(vslc PRIVATE /W4)
else()
    # additional warnings
    target_compile_options(vslc PRIVATE -Wall)
    # === Address sanitizer
    #target_compile_options(vslc PRIVATE -fsanitize=address -fno-omit-frame-pointer -g)
    #target_link_options(vslc PRIVATE -fsanitize=address)
endif()
