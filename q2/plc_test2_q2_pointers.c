#include <stdio.h>

#define ARRAY_DIM_SIZE1 100
#define ARRAY_DIM_SIZE2 200

int array[ARRAY_DIM_SIZE1][ARRAY_DIM_SIZE2];

int main() {
    int x = 0;
    for (int(* pi)[ARRAY_DIM_SIZE2] = array; pi != array + ARRAY_DIM_SIZE1; pi++) {
        for (int* pj = *pi; pj != *pi + ARRAY_DIM_SIZE2; pj++) {
            *pj = x++;
        }
    }

    for (int(* pi)[ARRAY_DIM_SIZE2] = array; pi != array + ARRAY_DIM_SIZE1; pi++) {
        for (int* pj = *pi; pj < *pi + ARRAY_DIM_SIZE2; pj++) {
            printf("%d\n", *pj);
        }
    }
    return 0;
}