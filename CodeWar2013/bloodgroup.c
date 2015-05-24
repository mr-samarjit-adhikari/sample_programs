#include <stdio.h>
#include <stdlib.h>

#define BLOOD_GROUP_TABLE_LEN  4
#define RRH_FACTOR_TABLE_LEN   2


typedef struct blood_type_marker_set{
    char    *first;
    char    *second;
    struct blood_type_marker_set* prev;
    struct blood_type_marker_set* next;
} bmset_t;

typedef struct rh_allele_set{
    char    *first;
    char    *second;
    struct rh_allele_set* prev;
    struct rh_allele_set* next;
} rhaset_t;


static bmset_t*
new_blood_marker_set(char* first, char* sencond){
    bmset_t  *bm = calloc(1,sizeof(bmset_t));
    if(NULL != bm){
        bm->first = first;
        bm->second = sencond;
    }
    else{
        perror("Failed to create new blood_marker!");
    }

    return bm;
}

static rhaset_t*
new_rh_allele_set(char *first, char* second){
    rhaset_t *rhaset = calloc(1,sizeof(rhaset_t));
    if(NULL != rhaset){
        rhaset->first = first;
        rhaset->second = second;
    }
    else{
        perror("Failed to create new rh_allele_set!");
    }

    return rhaset;
}

typedef struct bloodgroup_type{
    bmset_t       *bmset_list;
    bmset_t       *head;
    unsigned int  count; /* To hold count of List element */
    char          *bloodg_name;
    void         (*add)(struct bloodgroup_type *self, bmset_t *bm);
} bloodg_t;

typedef struct rh_factor_type{
    rhaset_t      *list;
    rhaset_t      *head;
    unsigned int  count;
    char          *name;
    void          (*add)(struct rh_factor_type* self, rhaset_t* rha);
} rrh_factor_t;

static void
add_bloodgroup_marker_set(bloodg_t *self,bmset_t *bm)
{
    bmset_t   *tmp_bmset = NULL;
    if(NULL == bm)return ;

    if( NULL == self->bmset_list){
        self->bmset_list = bm;
        bm->next = bm;
        bm->prev = bm;
        self->head = bm;
    }
    else{
        tmp_bmset = self->head->next;
        bm->prev = self->head;
        self->head->next = bm;
        bm->next = tmp_bmset;       
        bm->next->prev = bm;
    }
}

static void
add_rh_allele_set(rrh_factor_t* self,rhaset_t *rha)
{
    rhaset_t   *tmp_rhaset = NULL;
    if(NULL == rha)return;

    if( NULL == self->list){
        self->list = rha;
        rha->next = rha;
        rha->prev = rha;
        self->head = rha;
    }
    else{
        tmp_rhaset = self->head->next;
        rha->prev = self->head;
        self->head->next = rha;
        rha->next = tmp_rhaset;       
        rha->next->prev = rha;
    }
}
static int
rrh_factor_init(rrh_factor_t* rhf,char *name)
{
    rhf -> add = add_rh_allele_set;
    rhf->name  = name;
}

static int
bloodgroup_init(bloodg_t *bg,char* name){
   bg -> add  = add_bloodgroup_marker_set; 
   bg->bloodg_name = name;
}

static void 
build_blood_group_table(bloodg_t** table,unsigned int length)
{
    bloodg_t  *bg = NULL;


    bg = calloc(1,sizeof(bloodg_t));
    if(NULL != bg){
        bloodgroup_init(bg,"A");
        bg->add(bg,new_blood_marker_set("A","A"));
        bg->add(bg,new_blood_marker_set("A","O"));
        bg->count += 2;
    }
    else{
        perror ("Could not allocate memory for bg A !.");
    }
    table[0] = bg;
    bg = NULL;

    bg = calloc(1,sizeof(bloodg_t));
    if(NULL != bg){
        bloodgroup_init(bg,"AB");
        bg->add(bg,new_blood_marker_set("A","B"));
        bg->count += 1;
    }
    else{
        perror ("Could not allocate memory for bg AB!.");
    }
    table[1] = bg;
    bg = NULL;

    bg = calloc(1,sizeof(bloodg_t));
    if(NULL != bg){
        bloodgroup_init(bg,"B");
        bg->add(bg,new_blood_marker_set("B","B"));
        bg->add(bg,new_blood_marker_set("B","O"));
        bg->count += 2;
    }
    else{
        perror ("Could not allocate memory for father_bg !.");
    }
    table[2] = bg;
    bg = NULL;

    bg = calloc(1,sizeof(bloodg_t));
    if(NULL != bg){
        bloodgroup_init(bg,"O");
        bg->add(bg,new_blood_marker_set("O","O"));
        bg->count += 1;
    }
    else{
        perror ("Could not allocate memory for father_bg !.");
    }
    table[3] = bg;
    bg = NULL;


}

build_rh_factor_table(rrh_factor_t** rrh_factor_table,unsigned int size)
{
    rrh_factor_t    *rrh_factor = NULL;

    rrh_factor = calloc(1,sizeof(rrh_factor_t));
    if(NULL != rrh_factor){
        rrh_factor_init(rrh_factor,"+");
        rrh_factor->add(rrh_factor,new_rh_allele_set("+","+"));
        rrh_factor->add(rrh_factor,new_rh_allele_set("+","-"));
        rrh_factor->add(rrh_factor,new_rh_allele_set("-","+"));
        rrh_factor->count += 3;
    }
    else{
        perror("Could not allocate memory for rrh_factor !");
    }
    rrh_factor_table[0] = rrh_factor;
    rrh_factor = NULL;

    rrh_factor = calloc(1,sizeof(rrh_factor_t));
    if(NULL != rrh_factor){
        rrh_factor_init(rrh_factor,"-");
        rrh_factor->add(rrh_factor,new_rh_allele_set("-","-"));
        rrh_factor->count += 1;
    }
    else{
        perror("Could not allocate memory for rrh_factor !");
    }
    rrh_factor_table[1] = rrh_factor;
    rrh_factor = NULL;
}

typedef struct input_set_type{
    char blood_group[10];
    char rh_factor[10];
    unsigned int output_req;
}input_set_t;

static void 
process_each_input_line(input_set_t *input,const char *line , unsigned int length)
{
    char  *ptr             = (char*)line;
    unsigned int    index  = 0;
    input_set_t     *args  = input; /* Blood group, Rh factor */
 
    /* find what is asking for */
    while(*ptr && index<3){
        switch(*ptr){
            case 'A':
                if(*(ptr+1) == 'B'){
                    snprintf(args[index].blood_group,3,"%s",ptr);
                    ptr++;
                }
                else{
                    snprintf(args[index].blood_group,2,"%s",ptr);
                }
                break;
            case 'B':
                snprintf(args[index].blood_group,2,"%s",ptr);
                break;
            case '?':
                args[index++].output_req = 1;
                break;
            case '+':
            case '-':
                snprintf(args[index++].rh_factor,2,"%s",ptr);
               break;
            default:
               break; 
        }
        ptr++;
    }
}

static bloodg_t*
get_blood_group_from_table(bloodg_t** table,const char* name)
{
    unsigned int  index = 0;

    for(index = 0;index < BLOOD_GROUP_TABLE_LEN; index++){
        if(!strcmp(table[index]->bloodg_name,name)){
            break;
        }
    }
    return table[index];
}

static void
add_unique_bloodgroup_marker_set(bloodg_t *self,bmset_t *bm)
{
    bmset_t   *tmp_bmset   = NULL;
    unsigned int ele_count = self->count;
    bmset_t   *ptr         = NULL;
    unsigned int  found    = 0;

    if(NULL == bm)return ;

    if( NULL == self->bmset_list){
        self->bmset_list = bm;
        bm->next = bm;
        bm->prev = bm;
        self->head = bm;
        self->count += 1;
    }
    else{
        ptr = self->head;
        while(ele_count--){
           if(!strcmp(ptr->first,bm->first)&& !strcmp(ptr->second,bm->second)){
                found = 1;break;
            } 
            ptr = ptr->next;
        }

        if(0 == found){
            tmp_bmset = self->head->next;
            bm->prev = self->head;
            self->head->next = bm;
            bm->next = tmp_bmset;       
            bm->next->prev = bm;
            self->count += 1;
        }
    }
}


static bloodg_t*
get_all_possible_bmsets(bmset_t*  fptr, bmset_t* mptr,bloodg_t** out)
{
    bloodg_t   *bmset_list  = NULL;
    if(NULL == *out){
        bmset_list = calloc(1,sizeof(bloodg_t));
        bmset_list->add = add_unique_bloodgroup_marker_set;
    }
    else{
        bmset_list = *out;
    }

    /* Using fptr->first */
    switch(fptr->first[0]){
        case 'A':
            switch(mptr->first[0]){
                case 'A':
                    bmset_list->add(bmset_list,new_blood_marker_set("A","A"));
                    break;
                case 'B':
                    bmset_list->add(bmset_list,new_blood_marker_set("A","B"));
                    break;
                case 'O':
                    bmset_list->add(bmset_list,new_blood_marker_set("A","O"));
                    break;
            }
            switch(mptr->second[0]){
                case 'A':
                    bmset_list->add(bmset_list,new_blood_marker_set("A","A"));
                    break;
                case 'B':
                    bmset_list->add(bmset_list,new_blood_marker_set("A","B"));
                    break;
                case 'O':
                    bmset_list->add(bmset_list,new_blood_marker_set("A","O"));
                    break;
            }
            break;
        case 'B':
            switch(mptr->first[0]){
                case 'A':
                    bmset_list->add(bmset_list,new_blood_marker_set("B","A"));
                    break;
                case 'B':
                    bmset_list->add(bmset_list,new_blood_marker_set("B","B"));
                    break;
                case 'O':
                    bmset_list->add(bmset_list,new_blood_marker_set("B","O"));
                    break;
            }
            switch(mptr->second[0]){
                case 'A':
                    bmset_list->add(bmset_list,new_blood_marker_set("B","A"));
                    break;
                case 'B':
                    bmset_list->add(bmset_list,new_blood_marker_set("B","B"));
                    break;
                case 'O':
                    bmset_list->add(bmset_list,new_blood_marker_set("B","O"));
                    break;
            }
            break;
        case 'O':
            switch(mptr->first[0]){
                case 'A':
                    bmset_list->add(bmset_list,new_blood_marker_set("O","A"));
                    break;
                case 'B':
                    bmset_list->add(bmset_list,new_blood_marker_set("O","B"));
                    break;
                case 'O':
                    bmset_list->add(bmset_list,new_blood_marker_set("O","O"));
                    break;
            }
            switch(mptr->second[0]){
                case 'A':
                    bmset_list->add(bmset_list,new_blood_marker_set("O","A"));
                    break;
                case 'B':
                    bmset_list->add(bmset_list,new_blood_marker_set("O","B"));
                    break;
                case 'O':
                    bmset_list->add(bmset_list,new_blood_marker_set("O","O"));
                    break;
            }
            break;
    }

    /* using fptr->second */
    switch(fptr->second[0]){
        case 'A':
            switch(mptr->first[0]){
                case 'A':
                    bmset_list->add(bmset_list,new_blood_marker_set("A","A"));
                    break;
                case 'B':
                    bmset_list->add(bmset_list,new_blood_marker_set("A","B"));
                    break;
                case 'O':
                    bmset_list->add(bmset_list,new_blood_marker_set("A","O"));
                    break;
            }
            switch(mptr->second[0]){
                case 'A':
                    bmset_list->add(bmset_list,new_blood_marker_set("A","A"));
                    break;
                case 'B':
                    bmset_list->add(bmset_list,new_blood_marker_set("A","B"));
                    break;
                case 'O':
                    bmset_list->add(bmset_list,new_blood_marker_set("A","O"));
                    break;
            }
            break;
        case 'B':
            switch(mptr->first[0]){
                case 'A':
                    bmset_list->add(bmset_list,new_blood_marker_set("B","A"));
                    break;
                case 'B':
                    bmset_list->add(bmset_list,new_blood_marker_set("B","B"));
                    break;
                case 'O':
                    bmset_list->add(bmset_list,new_blood_marker_set("B","O"));
                    break;
            }
            switch(mptr->second[0]){
                case 'A':
                    bmset_list->add(bmset_list,new_blood_marker_set("B","A"));
                    break;
                case 'B':
                    bmset_list->add(bmset_list,new_blood_marker_set("B","B"));
                    break;
                case 'O':
                    bmset_list->add(bmset_list,new_blood_marker_set("B","O"));
                    break;
            }
            break;
        case 'O':
            switch(mptr->first[0]){
                case 'A':
                    bmset_list->add(bmset_list,new_blood_marker_set("O","A"));
                    break;
                case 'B':
                    bmset_list->add(bmset_list,new_blood_marker_set("O","B"));
                    break;
                case 'O':
                    bmset_list->add(bmset_list,new_blood_marker_set("O","O"));
                    break;
            }
            switch(mptr->second[0]){
                case 'A':
                    bmset_list->add(bmset_list,new_blood_marker_set("O","A"));
                    break;
                case 'B':
                    bmset_list->add(bmset_list,new_blood_marker_set("O","B"));
                    break;
                case 'O':
                    bmset_list->add(bmset_list,new_blood_marker_set("O","O"));
                    break;
            }
            break;
    }

    /* Using mptr->first */
    switch(mptr->first[0]){
        case 'A':
            switch(fptr->first[0]){
                case 'A':
                    bmset_list->add(bmset_list,new_blood_marker_set("A","A"));
                    break;
                case 'B':
                    bmset_list->add(bmset_list,new_blood_marker_set("A","B"));
                    break;
                case 'O':
                    bmset_list->add(bmset_list,new_blood_marker_set("A","O"));
                    break;
            }
            switch(fptr->second[0]){
                case 'A':
                    bmset_list->add(bmset_list,new_blood_marker_set("A","A"));
                    break;
                case 'B':
                    bmset_list->add(bmset_list,new_blood_marker_set("A","B"));
                    break;
                case 'O':
                    bmset_list->add(bmset_list,new_blood_marker_set("A","O"));
                    break;
            }
            break;
        case 'B':
            switch(fptr->first[0]){
                case 'A':
                    bmset_list->add(bmset_list,new_blood_marker_set("B","A"));
                    break;
                case 'B':
                    bmset_list->add(bmset_list,new_blood_marker_set("B","B"));
                    break;
                case 'O':
                    bmset_list->add(bmset_list,new_blood_marker_set("B","O"));
                    break;
            }
            switch(fptr->second[0]){
                case 'A':
                    bmset_list->add(bmset_list,new_blood_marker_set("B","A"));
                    break;
                case 'B':
                    bmset_list->add(bmset_list,new_blood_marker_set("B","B"));
                    break;
                case 'O':
                    bmset_list->add(bmset_list,new_blood_marker_set("B","O"));
                    break;
            }
            break;
        case 'O':
            switch(fptr->first[0]){
                case 'A':
                    bmset_list->add(bmset_list,new_blood_marker_set("O","A"));
                    break;
                case 'B':
                    bmset_list->add(bmset_list,new_blood_marker_set("O","B"));
                    break;
                case 'O':
                    bmset_list->add(bmset_list,new_blood_marker_set("O","O"));
                    break;
            }
            switch(fptr->second[0]){
                case 'A':
                    bmset_list->add(bmset_list,new_blood_marker_set("O","A"));
                    break;
                case 'B':
                    bmset_list->add(bmset_list,new_blood_marker_set("O","B"));
                    break;
                case 'O':
                    bmset_list->add(bmset_list,new_blood_marker_set("O","O"));
                    break;
            }
            break;
    }
    /* Using mptr->sencond[0]*/
    switch(mptr->second[0]){
        case 'A':
            switch(fptr->first[0]){
                case 'A':
                    bmset_list->add(bmset_list,new_blood_marker_set("A","A"));
                    break;
                case 'B':
                    bmset_list->add(bmset_list,new_blood_marker_set("A","B"));
                    break;
                case 'O':
                    bmset_list->add(bmset_list,new_blood_marker_set("A","O"));
                    break;
            }
            switch(fptr->second[0]){
                case 'A':
                    bmset_list->add(bmset_list,new_blood_marker_set("A","A"));
                    break;
                case 'B':
                    bmset_list->add(bmset_list,new_blood_marker_set("A","B"));
                    break;
                case 'O':
                    bmset_list->add(bmset_list,new_blood_marker_set("A","O"));
                    break;
            }
            break;
        case 'B':
            switch(fptr->first[0]){
                case 'A':
                    bmset_list->add(bmset_list,new_blood_marker_set("B","A"));
                    break;
                case 'B':
                    bmset_list->add(bmset_list,new_blood_marker_set("B","B"));
                    break;
                case 'O':
                    bmset_list->add(bmset_list,new_blood_marker_set("B","O"));
                    break;
            }
            switch(fptr->second[0]){
                case 'A':
                    bmset_list->add(bmset_list,new_blood_marker_set("B","A"));
                    break;
                case 'B':
                    bmset_list->add(bmset_list,new_blood_marker_set("B","B"));
                    break;
                case 'O':
                    bmset_list->add(bmset_list,new_blood_marker_set("B","O"));
                    break;
            }
            break;
        case 'O':
            switch(fptr->first[0]){
                case 'A':
                    bmset_list->add(bmset_list,new_blood_marker_set("O","A"));
                    break;
                case 'B':
                    bmset_list->add(bmset_list,new_blood_marker_set("O","B"));
                    break;
                case 'O':
                    bmset_list->add(bmset_list,new_blood_marker_set("O","O"));
                    break;
            }
            switch(fptr->second[0]){
                case 'A':
                    bmset_list->add(bmset_list,new_blood_marker_set("O","A"));
                    break;
                case 'B':
                    bmset_list->add(bmset_list,new_blood_marker_set("O","B"));
                    break;
                case 'O':
                    bmset_list->add(bmset_list,new_blood_marker_set("O","O"));
                    break;
            }
            break;
    }

    return bmset_list;
}

static  char*
get_blood_group_name_from_table(bloodg_t** table,bmset_t* bm)
{
    bmset_t*      ptr   = NULL;
    unsigned int  index = 0;
    unsigned int  found = 0;
    unsigned int  ele_count = 0;
    char*         ret_str = NULL;

    while(index < BLOOD_GROUP_TABLE_LEN){
        ptr = table[index]->head;
        ele_count = table[index]->count;

        while(ele_count--){
           if((ptr->first[0] == bm->first[0] )&& (ptr->second[0] == bm->second[0])){
                found = 1;break;
            } 
            ptr = ptr->next;
        }
        if(1 == found){
            ret_str = table[index]->bloodg_name;
            break;
        }
        index++;
    }

    return ret_str;
}

static rrh_factor_t*
get_rrhfactor_from_table(rrh_factor_t**  table,char* key)
{
    unsigned int index = 0;
    
    for(index=0;index<RRH_FACTOR_TABLE_LEN;index++){
        if(!strcmp(table[index]->name,key)){
            break;
        }
    }

    return table[index];
}

static void
add_unique_rha_set(rrh_factor_t* self,rhaset_t *rha)
{
    rhaset_t   *tmp_rhaset   = NULL;
    unsigned int ele_count = self->count;
    rhaset_t   *ptr         = NULL;
    unsigned int  found    = 0;

    if(NULL == rha)return ;

    if( NULL == self->list){
        self->list = rha;
        rha->next = rha;
        rha->prev = rha;
        self->head = rha;
        self->count += 1;
    }
    else{
        ptr = self->head;
        while(ele_count--){
           if((ptr->first[0] == rha->first[0] )&& (ptr->second[0] == rha->second[0])){
                found = 1;break;
            } 
            ptr = ptr->next;
        }

        if(0 == found){
            tmp_rhaset = self->head->next;
            rha->prev = self->head;
            self->head->next = rha;
            rha->next = tmp_rhaset;       
            rha->next->prev = rha;
            self->count += 1;
        }
    }

}

static rrh_factor_t*
get_all_possible_rhasets(rhaset_t*  fptr, rhaset_t* mptr,rrh_factor_t** out)
{
    rrh_factor_t   *rha_list  = NULL;

    if(NULL == *out){
        rha_list = calloc(1,sizeof(rrh_factor_t));
        rha_list->add = add_unique_rha_set;
    }
    else{
        rha_list = *out;
    }

    switch(fptr->first[0]){
        case '+':
            switch(mptr->first[0]){
                case '+':
                    rha_list->add(rha_list,new_rh_allele_set("+","+"));
                    break;
                case '-':
                    rha_list->add(rha_list,new_rh_allele_set("+","-"));
                    break;
            }
            switch(mptr->second[0]){
                case '+':
                    rha_list->add(rha_list,new_rh_allele_set("+","+"));
                    break;
                case '-':
                    rha_list->add(rha_list,new_rh_allele_set("+","-"));
                    break;
            }
            break;
        case '-':
            switch(mptr->first[0]){
                case '+':
                    rha_list->add(rha_list,new_rh_allele_set("-","+"));
                    break;
                case '-':
                    rha_list->add(rha_list,new_rh_allele_set("-","-"));
                    break;
            }
            switch(mptr->second[0]){
                case '+':
                    rha_list->add(rha_list,new_rh_allele_set("-","+"));
                    break;
                case '-':
                    rha_list->add(rha_list,new_rh_allele_set("-","-"));
                    break;
            }
            break;
    }
    switch(fptr->second[0]){
        case '+':
            switch(mptr->first[0]){
                case '+':
                    rha_list->add(rha_list,new_rh_allele_set("+","+"));
                    break;
                case '-':
                    rha_list->add(rha_list,new_rh_allele_set("+","-"));
                    break;
            }
            switch(mptr->second[0]){
                case '+':
                    rha_list->add(rha_list,new_rh_allele_set("+","+"));
                    break;
                case '-':
                    rha_list->add(rha_list,new_rh_allele_set("+","-"));
                    break;
            }
            break;
        case '-':
            switch(mptr->first[0]){
                case '+':
                    rha_list->add(rha_list,new_rh_allele_set("-","+"));
                    break;
                case '-':
                    rha_list->add(rha_list,new_rh_allele_set("-","-"));
                    break;
            }
            switch(mptr->second[0]){
                case '+':
                    rha_list->add(rha_list,new_rh_allele_set("-","+"));
                    break;
                case '-':
                    rha_list->add(rha_list,new_rh_allele_set("-","-"));
                    break;
            }
            break;
    }
  
     switch(mptr->first[0]){
        case '+':
            switch(fptr->first[0]){
                case '+':
                    rha_list->add(rha_list,new_rh_allele_set("+","+"));
                    break;
                case '-':
                    rha_list->add(rha_list,new_rh_allele_set("+","-"));
                    break;
            }
            switch(fptr->second[0]){
                case '+':
                    rha_list->add(rha_list,new_rh_allele_set("+","+"));
                    break;
                case '-':
                    rha_list->add(rha_list,new_rh_allele_set("+","-"));
                    break;
            }
            break;
        case '-':
            switch(fptr->first[0]){
                case '+':
                    rha_list->add(rha_list,new_rh_allele_set("-","+"));
                    break;
                case '-':
                    rha_list->add(rha_list,new_rh_allele_set("-","-"));
                    break;
            }
            switch(fptr->second[0]){
                case '+':
                    rha_list->add(rha_list,new_rh_allele_set("-","+"));
                    break;
                case '-':
                    rha_list->add(rha_list,new_rh_allele_set("-","-"));
                    break;
            }
            break;
    }

     switch(mptr->second[0]){
        case '+':
            switch(fptr->first[0]){
                case '+':
                    rha_list->add(rha_list,new_rh_allele_set("+","+"));
                    break;
                case '-':
                    rha_list->add(rha_list,new_rh_allele_set("+","-"));
                    break;
            }
            switch(fptr->second[0]){
                case '+':
                    rha_list->add(rha_list,new_rh_allele_set("+","+"));
                    break;
                case '-':
                    rha_list->add(rha_list,new_rh_allele_set("+","-"));
                    break;
            }
            break;
        case '-':
            switch(fptr->first[0]){
                case '+':
                    rha_list->add(rha_list,new_rh_allele_set("-","+"));
                    break;
                case '-':
                    rha_list->add(rha_list,new_rh_allele_set("-","-"));
                    break;
            }
            switch(fptr->second[0]){
                case '+':
                    rha_list->add(rha_list,new_rh_allele_set("-","+"));
                    break;
                case '-':
                    rha_list->add(rha_list,new_rh_allele_set("-","-"));
                    break;
            }
            break;
    }

    return rha_list;
}

static char*
get_rrh_factor_name_from_table(rrh_factor_t** table,rhaset_t* rha)
{
    rhaset_t*      ptr   = NULL;
    unsigned int  index = 0;
    unsigned int  found = 0;
    unsigned int  ele_count = 0;
    char*         ret_str = NULL;

    while(index < RRH_FACTOR_TABLE_LEN){
        ptr = table[index]->head;
        ele_count = table[index]->count;

        while(ele_count--){
           if((ptr->first[0] == rha->first[0] )&& (ptr->second[0] == rha->second[0])){
                found = 1;break;
            } 
            ptr = ptr->next;
        }
        if(1 == found){
            ret_str = table[index]->name;
            break;
        }
        index++;
    }

    return ret_str;

}

void 
child_blood_group_print(input_set_t* args,bloodg_t** bg_table,rrh_factor_t** rrh_factor_table)
{

    unsigned int index =0;
    bloodg_t*      father     = get_blood_group_from_table(bg_table,args[0].blood_group);
    rrh_factor_t*  father_rrh = get_rrhfactor_from_table(rrh_factor_table,args[0].rh_factor);
    bloodg_t*      mother     = get_blood_group_from_table(bg_table,args[1].blood_group);
    rrh_factor_t*  mother_rrh = get_rrhfactor_from_table(rrh_factor_table,args[1].rh_factor);
    unsigned int father_bmset_count = father->count;
    unsigned int mother_bmset_count = mother->count;
    unsigned int father_rhaset_count = father_rrh->count;
    unsigned int mother_rhaset_count = mother_rrh->count;
    bloodg_t*         outlist = NULL;
    rrh_factor_t*     outlist_rrh = NULL;
    bmset_t*          outlist_ptr = NULL;
    rhaset_t*         outlist_rrhptr = NULL;
    unsigned int      outlistcount = 0;
    unsigned int      outlist_rrhcount = 0;
    char*             child_bg = NULL;
    char              child_bglist[10][10] = {{'\0'}};
    char*             child_rrh = NULL;
    char              child_rrhlist[10][10] = {{'\0'}};
    rhaset_t          *fptr_rha = NULL;
    rhaset_t          *mptr_rha = NULL;
   
    bmset_t  *fptr = father->head; 
    bmset_t  *mptr = mother->head; 
    while(father_bmset_count--){
        mother_bmset_count = mother->count;
        while(mother_bmset_count--){
            outlist = get_all_possible_bmsets(fptr,mptr,&outlist);
            mptr = mptr->next;
        }
        fptr = fptr->next;
    }

    outlistcount = outlist->count;
    outlist_ptr  = outlist->head;
    index = 0;
    while(outlistcount--){
        if(NULL != (child_bg = get_blood_group_name_from_table
                                        (bg_table,outlist_ptr)))
        {
            snprintf(child_bglist[index++],10,"%s",child_bg);
        }
        outlist_ptr = outlist_ptr->next;
    }

    /* Now calculate the rh factor */
    fptr_rha = father_rrh->head;
    mptr_rha = mother_rrh->head;
    while(father_rhaset_count--){
        mother_rhaset_count = mother_rrh->count;
        while(mother_rhaset_count--){
            outlist_rrh = get_all_possible_rhasets(fptr_rha,mptr_rha,&outlist_rrh);
            mptr_rha = mptr_rha->next;
        }
        fptr_rha = fptr_rha->next;
    }

    outlist_rrhcount = outlist_rrh->count;
    outlist_rrhptr   = outlist_rrh->head;
    index = 0;
    while(outlist_rrhcount --){
        if(NULL != (child_rrh = get_rrh_factor_name_from_table
                                            (rrh_factor_table,outlist_rrhptr)))
        {
            snprintf(child_rrhlist[index++],10,"%s",child_rrh);
        }
        outlist_rrhptr = outlist_rrhptr->next;
    }

    /* Final output */
    bloodg_t *final_list = calloc(1,sizeof(bloodg_t));
    final_list->add = add_unique_bloodgroup_marker_set;

    index = 0;
    while(index < BLOOD_GROUP_TABLE_LEN){
        bloodg_t          *ptr = bg_table[index];
        unsigned int ele_count = bg_table[index]->count;
        char*             name = ptr->bloodg_name;

        int i = 0;
        while(child_bglist[i][0] != '\0'){
            if(!strcmp(name,child_bglist[i])){
                int j = 0;
                while(child_rrhlist[j][0] != '\0'){
                    if(!strcmp("+",child_rrhlist[j])){
                        final_list->add(final_list,new_blood_marker_set(name,"+"));
                    }
                    j++;
                }
                j = 0;
                while(child_rrhlist[j][0] != '\0'){
                    if(!strcmp("-",child_rrhlist[j])){
                        final_list->add(final_list,new_blood_marker_set(name,"-"));
                    }
                    j++;
                }
 
            }
            i++;
        }
        index++;
    }

    unsigned int final_list_count = final_list->count;
    bmset_t      *ptr = final_list->head;
    while(final_list_count--){
        printf("%s%s ",ptr->first,ptr->second);
        ptr = ptr->prev;
    }

    printf("\n");
}

void 
parent_blood_group_print(input_set_t* args,bloodg_t** bg_table,rrh_factor_t** rrh_factor_table)
{
    unsigned int   index =0;
    index = args[index].required_argument?index:index+1;
    bloodg_t*      parent_bg     = get_blood_group_from_table(bg_table,args[index].blood_group);
    rrh_factor_t*  parent_rrh    = get_rrhfactor_from_table(rrh_factor_table,args[index].rh_factor);
    bloodg_t*      outlist_bg    = NULL;
    unsigned int   count         = 0;
    bmset_t*       outbg_ptr     = NULL;
    char*          possible_bg   = NULL;
    char           possible_bglist[10][10]={{'\0'}};

    unsigned int   parent_bgcount = parent_bg->count;
    bmset_t*       ptr            = parent_bg->head;
    while(parent_bgcount--){
        index = 0;
        while(index<BLOOD_GROUP_TABLE_LEN){
            bmset_t           *optr = bg_table[index]->head;
            unsigned int      optr_count = bg_table[index]->count;
            while(optr_count--){
                outlist_bg = get_all_possible_bmsets(ptr,optr,&outlist_bg);
                optr = optr->next;
            }
            
        }
        ptr = ptr->next;
    }

    count      = outlist_bg->count;
    outbg_ptr  = outlist_bg->head;
    index = 0;
    while(count--){
        if(NULL != (possible_bg = get_blood_group_name_from_table
                                        (bg_table,outbg_ptr)))
        {
            if(!strcmp(args[3].blood_group,possible_bg)){
                snprintf(possible_bglist[index++],10,"%s",possible_bg);
            }
        }
        outbg_ptr = outbg_ptr->next;
    }
   
 
}

/* Main starts hear */
main()
{
#define LINE_LEN   81
    unsigned int   input_count = 0;
    char           line[LINE_LEN]      = {'\0'};
    bloodg_t*      blood_group_table[BLOOD_GROUP_TABLE_LEN] = {'\0'};
    rrh_factor_t*  rrh_factor_table[RRH_FACTOR_TABLE_LEN]   = {'\0'};
    input_set_t    input_args[3] = {{{'\0'},{'\0'},0},{{'\0'},{'\0'},0}
                                   ,{{'\0'},{'\0'},0}}; /* Blood group, Rh factor */

    build_blood_group_table(blood_group_table,BLOOD_GROUP_TABLE_LEN);
    build_rh_factor_table(rrh_factor_table,RRH_FACTOR_TABLE_LEN);

    scanf("%d",&input_count); 

    while(input_count+1 > 0){
        fgets(line,LINE_LEN,stdin);
        process_each_input_line(input_args,line,LINE_LEN);

        if(input_args[2].output_req){
            child_blood_group_print(input_args,blood_group_table,rrh_factor_table);
        }
        else{
            parent_blood_group_print(input_args,blood_group_table,rrh_factor_table);
        }

        input_count --;
    }
}
