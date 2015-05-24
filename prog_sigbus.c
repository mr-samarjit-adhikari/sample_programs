#include <stdio.h>

int main(int argc, char **argv)
{
    int testvar = 0x12345678;
    int *testvarp;

    testvarp = &testvar;
    printf("testvarp was %lx\n", testvarp);
    printf("testvar was %lx\n", *testvarp);

    testvarp = (int *)(((char *)testvarp) + 1);
    printf("testvarp is %lx\n", testvarp);
    printf("testvar is %lx\n", *testvarp);
 
} 

