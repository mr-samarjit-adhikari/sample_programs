/**
* Finding shortest path between 2 points.
* Implemenetd for CodeWar 2013
*/

#include <stdio.h>
#include <stdlib.h>

#define MAX_NODE_COUNT      200

typedef enum bool_type{
    FALSE,
    TRUE
} bool_t;

typedef struct dij_node_struct{
    int         node_id;
    int         order_id;
    int         temp_label;
    int         final_label;
    bool_t      visited;
    struct dij_node_struct* peer_list[MAX_NODE_COUNT];
    int         next_index;
} node_t;

typedef struct dijks_list_element{
    node_t*                      data;
    struct dijks_list_element*   next;
    struct dijks_list_element*   prev;
    struct dijks_list_element*   head;
    unsigned int                 count;
    void                         (*insert)(struct dijks_list_element*, node_t* key);
    struct dijks_list_element*   (*pop)(struct dijks_list_element*);
} dijks_list_node_t;

unsigned int cost_matrix[MAX_NODE_COUNT][MAX_NODE_COUNT];

void
insert_into_dijks_list(dijks_list_node_t* head,node_t* key){

    dijks_list_node_t*    tmp      = NULL;
    dijks_list_node_t*    tmp_next = NULL;
    bool_t                found    = FALSE;
    unsigned int          count    = head->count;
    dijks_list_node_t*    ptr      = NULL;

    ptr = head->next;
    while(count--){
        if(ptr->data == key){
            found = TRUE;
            break;
        }
        ptr = ptr->next;
    }

    if(FALSE == found){
        tmp = calloc(1,sizeof(dijks_list_node_t));
        tmp->data = key;

        tmp_next   = head->next;
        head->next = tmp;
        tmp->prev  = head;
        tmp->next  = tmp_next;       
        tmp_next->prev = tmp;
        head->count++;
    }
}

/* Find minimum label and return it */
dijks_list_node_t*
pop_from_dijks_list(dijks_list_node_t* head)
{
    unsigned int  local_count = head->count;
    dijks_list_node_t*   ptr  = head->next;
    dijks_list_node_t*   ret  = ptr;
    int                  min  = ptr->data->temp_label;
    dijks_list_node_t*   tmp  = NULL;

    while(local_count--){
        if(ptr->data->temp_label<min){
            min = ptr->data->temp_label;
            ret = ptr;
        }
        ptr = ptr->next;
    }

    /* pop  the value now */
    ret->prev->next = ret->next;
    ret->next->prev = ret->prev;
    ret -> next = ret->prev = NULL;

    head->count--;

    return ret;
}

void init_dijks_list(dijks_list_node_t* list){
    list -> count = 0;
    list -> head  = list;
    list -> next  = list;
    list -> prev  = list;
    list-> insert = insert_into_dijks_list;
    list-> pop    = pop_from_dijks_list;
}

node_t*
find_node_in_container(node_t** list,int len,int key)
{
    int         index = 0;
    node_t*     node  = NULL;

    for(index =0;list[index] != NULL;index++){
        if(key == list[index]->node_id){
            node = list[index];
            break;
        }
    }

    return node;
}

void
insert_into_peer_list(node_t*  in_node,node_t* key){
    in_node->peer_list[in_node->next_index++] = key;
}

node_t*
build_graph(int src)
{
    
    int         row = 0;
    int         col = 0;
    bool_t      flag      = FALSE;   
    node_t*     start     = NULL;
    node_t*     row_start = NULL;
    node_t*     tmp       = NULL;
    node_t*     container[MAX_NODE_COUNT] = {'\0'};
    int         container_index = 0;
 
    for(row=0;row<MAX_NODE_COUNT;row++){
        for(col=0;col<MAX_NODE_COUNT;col++){
            if(0 != cost_matrix[row][col]){
                if(NULL == (row_start = 
                   find_node_in_container(container,MAX_NODE_COUNT,row)))
                {
                    row_start = calloc(1,sizeof(node_t));
                    row_start->node_id = row;
                    container[container_index++] = row_start;
                }

                if(NULL == (tmp = 
                   find_node_in_container(container,MAX_NODE_COUNT,col)))
                {
                    tmp = calloc(1,sizeof(node_t));
                    tmp->node_id = col;
                    container[container_index++] = tmp;
                }
                insert_into_peer_list(row_start,tmp);
            }
        }

        if(row == src){
            start = row_start;
        }
    }
    
    return start;
}

node_t*
dijkstra_call(int src,int dst,int node_count,node_t* start,
                   dijks_list_node_t* data_list)
{
    int     curr_order_id   = 1;
    int     index           = 0;
    dijks_list_node_t* tmp  = NULL;
    node_t*            node = NULL;
    node_t*            peer = NULL;
    node_t*            end  = NULL;
    int          temp_label = 0;

    /* Initialize the start */
    start->final_label = 0;
    start->visited = TRUE;
    start->order_id = curr_order_id;
    while((node = start->peer_list[index++]) != NULL){
        node->temp_label = start->final_label +
                           cost_matrix[start->node_id][node->node_id];   
        data_list->insert(data_list,node);
    }

    while(curr_order_id < node_count){
        tmp = data_list->pop(data_list);
        node = tmp->data;
        node -> final_label = node -> temp_label;
        node -> order_id = ++curr_order_id;
        node -> visited = TRUE;
        if(node->node_id == dst) end = node;
        
        index = 0;
        while((peer = node->peer_list[index++]) != NULL){
            if(peer->visited == FALSE){
                temp_label = node->final_label + cost_matrix[node->node_id][peer->node_id];
                if(0 == peer->temp_label){ // Not have any valid value
                    peer->temp_label = temp_label;
                }
                else{
                    if(temp_label < peer->temp_label){
                        peer->temp_label = temp_label;
                    }
                }
                data_list->insert(data_list,peer);
            }
        }

        if(data_list != tmp){
            free(tmp);
        }
    }

    return end;
}


main()
{
    int     index     = 0;
    int     total_cost= 0;
    int     index_row = 0;
    int     node_count= 0;
    int     cost      = 0;
    int     id1       = 0;
    int     id2       = 0;
    int     src       = 0;
    int     dst       = 0;
    int     path_index= 0;
    node_t* node      = NULL;
    node_t* start     = NULL;
    node_t* start_tmp = NULL;
    node_t* end_tmp   = NULL;
    node_t* end       = NULL;
    char    line[80+1]  = {'\0'};
    int     path[MAX_NODE_COUNT] = {'\0'};
    dijks_list_node_t *dijks_head_list = calloc(1,sizeof(dijks_list_node_t));
    init_dijks_list(dijks_head_list);

    scanf("%d",&node_count);
    if(node_count > MAX_NODE_COUNT){
        printf("The programe supports only #%d nodes\n",MAX_NODE_COUNT);
        exit(0);
    }

    while(fgets(line,sizeof(line),stdin)){
        if(2 == sscanf(line,"%d %d %d",&id1,&id2,&cost))
        {
            src = id1;
            dst = id2;
        }
        else{
            cost_matrix[id1][id2] = cost_matrix[id2][id1] = cost;
        }
    }

    start = build_graph(src);
    end   = dijkstra_call(src,dst,node_count,start,dijks_head_list);

    /* Now print the path and cost */
    start_tmp  = start;
    end_tmp    = end;
    path[path_index++] = end->node_id;

    while(end_tmp!=start_tmp){
        index = 0;
        while((node = end_tmp->peer_list[index++]) != NULL){
            if(node->final_label == 
                (end_tmp->final_label - cost_matrix[end_tmp->node_id][node->node_id])){
                path[path_index++] = node->node_id;
                total_cost += cost_matrix[end_tmp->node_id][node->node_id];
                break;
            }
        }
        end_tmp = node;
    }

    index = 0;
    for(index=path_index-1;index>=0;index--){
        printf("%d ",path[index]);
    }
    printf("%d\n",total_cost);
}
