#include <stdio.h>

void 
find_solution(unsigned int N,unsigned int *sol,unsigned int sol_size)
{
    unsigned int     n,m,o = 0;

    for(n=1 ; n < N ; n++){
        for(m=1 ; m < (N-n) ; m++){
            o = N - n - m;
            /*Directly check here.*/
            if((n*4 + m*0.5 + o*0.25) == N){
                sol[0] = n;
                sol[1] = m;
                sol[2] = o;
            }
        }
    }
}


main()

{
    unsigned int    var     = 0;
    char            buf[50] = {'\0'};
    unsigned int    solution[3]  = {'\0'};

    while(fgets(buf,sizeof(buf),stdin)){
        sscanf(buf,"%u",&var);
        find_solution(var,solution,3);
        if(solution[0] == '\0'){
            printf("No Solution found \n");
        }
        else{
            printf("%d coins of 4 asiap each\n",solution[0]);
            printf("%d coins of 1/2 asiap each\n",solution[1]);
            printf("%d coins of 1/4 asiap each\n",solution[2]);
        }
        printf("===\n");
    }
}

