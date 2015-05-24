/**
 * This is a sample programe for checking 
 * diffrent __attribute__ 
 */

#include <stdio.h>

struct s1{
    char c1;
    int  i __attribute__((packed));
    char c2;
    int  i2;
};

struct s2{
    unsigned char type;
    unsigned char code;
    short         cksum;
    short         id;
    short         seq;
    unsigned int  otime;
    unsigned int  rtime;
    unsigned int  ttime;
    unsigned char data[0];
    
};

main()
{
    printf(" Size of s1 =%d\n",sizeof(struct s1));
    printf(" Size of s2 =%d\n",sizeof(struct s2));
}
