#include <stdio.h>

struct list_head{
    struct list_head *next,*prev;
};
typedef struct list_head list_head_t;

struct my_queue{
    struct list_head head[0];
    //struct list_head *head;
};
typedef struct my_queue my_queue_t;

main(){
    my_queue_t   q;
    printf("Size of my_queue = %d\n",sizeof(q));
}

