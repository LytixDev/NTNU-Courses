/* Written by Nicolai H. Brand */


#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <assert.h>

#define u8 uint8_t
#define u16 uint16_t

#define DELIMITER 28
#define BWT_DELIM 0x3
#define u8_MAX 255

size_t glob_cols_count;

struct BinaryData {
    u8 *bytes;
    size_t nbytes;
};

static void print_2d(u8 **arr, size_t n)
{
    for (size_t i = 0; i < n; i++) {
        for (size_t j = 0; j < n; j++) {
            if ((char)arr[i][j] != '\n')
                printf("%c", arr[i][j]);
            else
                printf("\\n");
        }
        putchar('\n');
    }

    //printf("---------\n");

    //for (size_t i = 0; i < n; i++) {
    //    for (size_t j = 0; j < n; j++) {
    //            printf("%d ", arr[i][j]);
    //    }
    //    putchar('\n');
    //}
}

static void binary_data_as_ascii(struct BinaryData *input)
{
    for (size_t i = 0; i < input->nbytes; i++)
        printf("%c", input->bytes[i]);

    putchar('\n');
}


struct BinaryData *run_length_coding(struct BinaryData *input)
{
    struct BinaryData *output = malloc(sizeof(struct BinaryData));
    output->nbytes = 0;
    output->bytes = malloc(sizeof(u8) * input->nbytes);

    u8 current;
    u8 occurences;
    for (size_t i = 0; i < input->nbytes; i++) {
        occurences = 1;
        current = input->bytes[i];
        while (current == input->bytes[++i]) {
            if (i == input->nbytes || occurences == u8_MAX)
                break;
            occurences++;
        }
        --i;
        if (occurences > 3) {
            output->bytes[output->nbytes++] = DELIMITER;
            output->bytes[output->nbytes++] = occurences;
            output->bytes[output->nbytes++] = current;
        } else {
            for (size_t j = 0; j < occurences; j++)
                output->bytes[output->nbytes++] = current;
        }
    }

    return output;
}

struct BinaryData * reverse_run_length_coding(struct BinaryData *input)
{
    u8 current;
    u8 occurences;
    u8 *bytes = malloc(sizeof(u8) * input->nbytes);
    size_t idx = 0;
    for (size_t i = 0; i < input->nbytes; i++) {
        current = input->bytes[i];
        if (current != DELIMITER) {
            bytes[idx++] = current;
        } else {
            occurences = input->bytes[++i];
            current = input->bytes[++i];
            for (size_t j = 0; j < occurences; j++)
                bytes[idx++] = current;
        }
    }

    struct BinaryData *output = malloc(sizeof(struct BinaryData));
    output->bytes = bytes;
    output->nbytes = idx;
    return output;
}

void right_rotate_and_wrap(size_t n, u8 arr[n], size_t d)
{
    u8 tmp[n];
    size_t k = 0;
 
    for (size_t i = d; i < n; i++) {
        tmp[k] = arr[i];
        k++;
    }
    for (size_t i = 0; i < d; i++) {
        tmp[k] = arr[i];
        k++;
    }
    for (size_t i = 0; i < n; i++) {
        arr[i] = tmp[i];
    }
}

int u8_2d_cmp(const void *a, const void *b)
{
    u8 *rowA = *(u8 **)a;
    u8 *rowB = *(u8 **)b;
    assert(glob_cols_count != -1);

    for (size_t i = 0; i < glob_cols_count; i++) {
        int diff = rowA[i] - rowB[i];
        if (diff != 0)
            return diff;
    }
    return 0;
}

void u8_2d_sort(size_t rows, u8 **arr)
{
    qsort(arr, rows, sizeof(arr[0]), u8_2d_cmp);
}

struct BinaryData *burrows_wheeler(struct BinaryData *input)
{
    /* + 1 because we need to add a sentinel value */
    size_t n = input->nbytes + 1;
    glob_cols_count = n;
    u8 base[n];
    struct BinaryData *output = malloc(sizeof(struct BinaryData));
    output->bytes = malloc(n * sizeof(u8));
    output->nbytes = n;

    u8 **rotations = malloc(n * sizeof(u8 *));
    for (size_t i = 0; i < n; i++)
        rotations[i] = malloc(n * sizeof(u8));

    memcpy(base, input->bytes, input->nbytes);
    base[n - 1] = BWT_DELIM;

    for (size_t i = 0; i < n; i++) {
        memcpy(rotations[i], base, n);
        right_rotate_and_wrap(n, rotations[i], i);
    }

    u8_2d_sort(n, rotations);
    for (size_t i = 0; i < n; i++) {
        output->bytes[i] = rotations[i][n - 1];
        free(rotations[i]);
    }

    free(rotations);

    return output;
}

struct BinaryData *reverse_burrows_wheeler(struct BinaryData *input)
{
    size_t n = input->nbytes;
    u8 **rotations = malloc(n * sizeof(u8 *));
    for (size_t i = 0; i < n; i++)
        rotations[i] = calloc(n, sizeof(u8));

    for (size_t i = 0; i < n; i++) {
        for (size_t j = 0; j < n; j++) {
            rotations[j][n - i - 1] = input->bytes[j];
        }
        u8_2d_sort(n, rotations);
    }

    struct BinaryData *output = malloc(sizeof(struct BinaryData));
    u8 *correct = NULL;
    for (size_t i = 0; i < n; i++) {
        if (rotations[i][n - 1] == BWT_DELIM) {
            correct = rotations[i];
            break;
        }
    }

    output->bytes = correct;
    output->nbytes = n;

    return output;
}


//TODO: if file to large, read and compress in smaller chunks
struct BinaryData *read_entire_file(char *file_name)
{
    FILE *fp;
    int fd;
    size_t fsize;

    fp = fopen(file_name, "rb");
    if (fp == NULL) {
        fprintf(stderr, "uh oh\n");
        return NULL;
    }
    fd = fileno(fp);

    struct BinaryData *bd = malloc(sizeof(struct BinaryData));

    /* get the size of the file in O(1) */
    struct stat st;
    fstat(fd, &st);
    fsize = st.st_size;
    bd->nbytes = fsize;

    //TODO mmap?
    bd->bytes = malloc(sizeof(u8) * fsize);
    fread(bd->bytes, 1, fsize, fp);
    fclose(fp);
    return bd;
}

int main(int argc, char **argv)
{
    glob_cols_count = -1;
    struct BinaryData *bd = read_entire_file("test_file");
    bd = run_length_coding(bd);
    bd = burrows_wheeler(bd);
    bd = reverse_burrows_wheeler(bd);
    bd = reverse_run_length_coding(bd);
    for (size_t i = 0; i < bd->nbytes - 1; i++)
        printf("%c", bd->bytes[i]);
}
