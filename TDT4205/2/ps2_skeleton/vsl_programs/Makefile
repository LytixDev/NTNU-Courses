VSLC := ../build/vslc

PS2_EXAMPLES := $(patsubst %.vsl, %.ast, $(wildcard ps2-parser/*.vsl))
PS2_GRAPHVIZ_SVG := $(patsubst %.vsl, %.svg, $(wildcard ps2-parser/*.vsl))
PS2_GRAPHVIZ_DOT := $(patsubst %.vsl, %.dot, $(wildcard ps2-parser/*.vsl))

.PHONY: all ps2 ps2-graphviz clean ps2-check

all: ps2

ps2: $(PS2_EXAMPLES)
ps2-graphviz: $(PS2_GRAPHVIZ_SVG) $(PS2_GRAPHVIZ_DOT)

ps2-parser/%.ast: ps2-parser/%.vsl $(VSLC)
	$(VSLC) -t < $< > $@

%.svg: %.vsl $(VSLC)
	GRAPHVIZ_OUTPUT=1 $(VSLC) -t < $< | dot -Tsvg -o $@

%.dot: %.vsl $(VSLC)
	GRAPHVIZ_OUTPUT=1 $(VSLC) -t < $< | dot -o $@
