#include <stdio.h>

#define ARRAY_DIM_SIZE1 100
#define ARRAY_DIM_SIZE2 200

int array[ARRAY_DIM_SIZE1][ARRAY_DIM_SIZE2];

int main() {
    int x = 0;
    for (int i = 0; i < ARRAY_DIM_SIZE1; i++) {
        for (int j = 0; j < ARRAY_DIM_SIZE2; j++) {
            array[i][j] = x++;
        }
    }

    for (int i = 0; i < ARRAY_DIM_SIZE1; i++) {
        for (int j = 0; j < ARRAY_DIM_SIZE2; j++) {
            printf("%d\n", array[i][j]);
        }
    }
    return 0;
}