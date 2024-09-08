CFLAGS = -Wall -Wpedantic -Wextra -Wshadow -g 
# -fsanitize=address -fsanitize=undefined

SRCS = main.c bitmap.c

TARGET = ps0

# No incremental build because I don't like it :-)
all: $(SRCS)
	$(CC) $(CFLAGS) $(SRCS) -o $(TARGET)

clean:
	rm -f $(TARGET)

.PHONY: all clean
