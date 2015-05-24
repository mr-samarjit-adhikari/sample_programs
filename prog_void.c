#include <stdio.h>

/*Test program to findout if we can store the type of structure */

struct genObj_struct{
    char    *type;
    void    *data;
    int     callback_ret;
};
typedef struct genObj_struct genObj_t;

int call(char* type){
    printf("call() get called! with type %s\n",type);
    return 0;
}

#define  PRINT(item) \
            if(0==strcmp(item.type,"int")){\
                printf("data is %d\n",*((int*)item.data));\
            }\
            else if(0==strcmp(item.type,"unsigned int")){\
                printf("data is %d\n",*((unsigned int*)item.data));\
            }\
            else if(0==strcmp(item.type,"char")){\
                printf("data is %d\n",*((char*)item.data));\
            }\
            else if(0==strcmp(item.type,"struct dummy")){\
                printf("dummy data is %d\n",((struct dummy*)item.data)->field1);\
            }\
            printf("type is %s\n",item.type)
 
#define     DEFINE_OPTION(type,data)   {#type,data,call(#type)}


struct dummy{
    int field1;
    int field2;
};

typedef union test_union{
    const char    *v1;
    int           *v2;
} test_union_t ;

main(){
    int     var1 = 1;
    int     var2 = 2;
    int     *ptr1 = &var1;
    int     *ptr2 = &var2;
    struct dummy    dvar = {5,5};
    struct dummy *d1 = &dvar;
    test_union_t union_var = {"abcd"};

    genObj_t    array[] = 
    { 
      DEFINE_OPTION(int,ptr1),
      DEFINE_OPTION(unsigned int,ptr2),
      DEFINE_OPTION(char,"STRING"),
      DEFINE_OPTION(struct dummy,d1),
    };

    PRINT(array[0]);
    PRINT(array[3]);
    printf("union_var =%s\n",union_var.v1);
}
