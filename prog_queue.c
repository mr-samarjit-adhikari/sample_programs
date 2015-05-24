/**
 *  This is Generic Queue implementation in C
 * 
**/

#include <stdio.h>
#include <stdlib.h>

struct Queue_node_type{
    struct Queue_node_type *next;
    struct Queue_node_type *prev; 
};
typedef struct Queue_node_type Queue_node_t;

struct Queue_type{
    void  (*init)(struct Queue_type* self);
    int   (*push)(struct Queue_type* self, Queue_node_t *obj);
    Queue_node_t* (*pop)(struct Queue_type* self);

    /* Private variable. Should not be used directly */
    unsigned int count;
    Queue_node_t *head;
};
typedef struct Queue_type Queue_t;

int 
push_Queue_list_imp(Queue_t* self, Queue_node_t* node){

    Queue_node_t* tmp  = NULL;
    Queue_node_t* next = NULL;
    Queue_node_t* prev = NULL;

    /* Only insert logic */
    if(0 == self->count){
        self->head = node;
        self->head->next = self->head;
        self->head->prev = self->head;
    }
    else{
        next = self->head;
        prev = self->head->next;
        /* Insert Operation */
        node->next = next;
        node->prev = prev;
        next->next = node;
        prev->prev = node;
    }

    self->count++;
}

Queue_node_t*
pop_Queue_list_imp(Queue_t* self){

    Queue_node_t*    obj  = NULL;
    Queue_node_t*    tmp  = NULL;
    Queue_node_t*    next = self->head->next;
    Queue_node_t*    prev = self->head->prev;

    if(0 == self->count){
        obj = NULL;
    }
    else if (1 == self->count){
        obj = self->head;
        obj->prev = obj->next = NULL;
        self->count -- ;
    }
    else{
        next = self->head->next;
        prev = self->head->prev;
        /* Delete Operation */
        next->next = prev;
        prev->next = next; 
        /* update head */
        obj = self->head;
        self->head = prev;
        /* Set NULL */
        obj->prev = obj->next = NULL;
        self->count -- ;
    }

    return obj;
}

void initialize_Queue_list_imp(Queue_t *self){
    self->count = 0 ;
    self->head  = NULL;
    self->push  = push_Queue_list_imp;
    self->pop   = pop_Queue_list_imp;
}

struct item_type {
    Queue_node_t node;   
    int data;
};
typedef struct item_type  item_t;

int main(){

    int  option = 0;
    int  value  = 0;
    Queue_t  my_queue;
    item_t   *item = NULL;
    Queue_node_t *node = NULL;

    /* Initialize Queue */
    initialize_Queue_list_imp(&my_queue);

    while(1)
    {
        printf("\n"
               "1. Push item. \n"
               "2. Pop  item. \n"
               "3. Quit\n");
        scanf("%d",&option);

        switch(option){
            case 1:
                printf("Enter Item: ");
                scanf("%d",&value);
                if(NULL == (item = calloc(1,sizeof(item_t)))){
                    perror("Calloc failed.\n");
                    goto END;
                }
                item->data = value;
                my_queue.push(&my_queue,&(item->node));
                break;
            case 2:
                if(NULL != (node = my_queue.pop(&my_queue))){
                    item =  (item_t*)node;
                    printf("Poped item = %d\n",item->data);
                    free(item);
                }
                break;
            case 3:
                goto END;
                break;
        }
    }

END:
    return 0;
}
