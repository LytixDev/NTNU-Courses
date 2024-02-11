%{
#include <vslc.h>
// The tokens defined in parser.y
#include "parser.h"

// parser.h contains some unused functions, ignore that
#pragma GCC diagnostic ignored "-Wunused-function"
%}
%option noyywrap
%option array
%option yylineno

WHITESPACE [\ \t\v\r\n]
COMMENT \/\/[^\n]+
QUOTED \"([^\"\n]|\\\")*\"
NUMBER [0-9]+

FUNC func
BEGIN begin
END end
RETURN return
PRINT print
BREAK break
IF if
THEN then
ELSE else
WHILE while
DO do
VAR var

IDENTIFIER [_a-zA-Z][_a-zA-Z0-9]*

%%
{WHITESPACE}+           { /* Eliminate whitespace */ }
{COMMENT}               { /* Eliminate comments */ }
{QUOTED}                { return STRING; }
{NUMBER}                { return NUMBER; }

{FUNC}                  { return FUNC; }
{BEGIN}                 { return OPENBLOCK; }
{END}                   { return CLOSEBLOCK; }
{RETURN}                { return RETURN; }
{PRINT}                 { return PRINT; }
{BREAK}                 { return BREAK; }
{IF}                    { return IF; }
{THEN}                  { return THEN; }
{ELSE}                  { return ELSE; }
{WHILE}                 { return WHILE; }
{DO}                    { return DO; }
{VAR}                   { return VAR; }

{IDENTIFIER}            { return IDENTIFIER; }

  /* Unknown chars get returned as single char tokens */
.                       { return yytext[0]; }
%%
