#include "bitmap.h"
#include <assert.h>
#include <stdio.h>
#include <stdlib.h>

#define XSIZE 2560 // Size of before image
#define YSIZE 2048

typedef struct {
    size_t x;
    size_t y;
    size_t pixel_width;
} ImageSize;


void invert_colors(uchar *image, ImageSize size)
{
    for (size_t y = 0; y < size.y; y++) {
        for (size_t x = 0; x < size.x * size.pixel_width; x += size.pixel_width) {
            size_t offset = y * size.x * size.pixel_width + x;
            for (size_t i = 0; i < size.pixel_width; i++) {
                image[offset + i] = 255 - image[offset + i];
            }
        }
    }
}

ImageSize upscale(uchar **image, ImageSize size, unsigned int factor)
{
    ImageSize scaled_size = { size.x * factor, size.y * factor, .pixel_width = size.pixel_width };
    // New image
    uchar *scaled_image = calloc(scaled_size.x * scaled_size.y * scaled_size.pixel_width, 1);

    for (size_t y = 0; y < size.y; y++) {
        for (size_t x = 0; x < size.x * size.pixel_width; x += size.pixel_width) {
            size_t offset = y * size.x * size.pixel_width + x;
            // Find the corresponding top-left pixel coordinate in the scaled image
            size_t x_new = x * factor;
            size_t y_new = y * factor;

            // Fill the corresponding pixel and neighbors
            for (size_t yy = y_new; yy < y_new + factor; yy++) {
                for (size_t xx = x_new; xx < x_new + factor * size.pixel_width;
                     xx += size.pixel_width) {
                    scaled_image[yy * scaled_size.x * size.pixel_width + xx + 0] = (*image)[offset];
                    scaled_image[yy * scaled_size.x * size.pixel_width + xx + 1] =
                        (*image)[offset + 1];
                    scaled_image[yy * scaled_size.x * size.pixel_width + xx + 2] =
                        (*image)[offset + 2];
                }
            }
        }
    }

    free(*image);
    *image = scaled_image;
    return scaled_size;
}


int main(void)
{
    ImageSize size = { .x = XSIZE, .y = YSIZE, .pixel_width = 3 };
    uchar *image = calloc(size.x * size.y * size.pixel_width, 1); // Three uchars per pixel (RGB)
    readbmp("before.bmp", image);

    invert_colors(image, size);
    size = upscale(&image, size, 2);

    savebmp("after.bmp", image, size.x, size.y);

    free(image);
    return 0;
}
