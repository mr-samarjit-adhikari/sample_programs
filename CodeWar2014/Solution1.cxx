#include <stdio.h>
#include <math.h>

static unsigned int prime_index = 0;
unsigned int get_next_prime()
{
    unsigned int prime_list[] = {2,3,5,7,11,13};
    return prime_list[prime_index++];
}
void reset_prime_index()
{
    prime_index = 0;
}

unsigned long int cal_improbability(unsigned long var)
{
    unsigned long int    ret_val = 0;

    if(var == 1){
        return 4;
    }

    ret_val = pow(get_next_prime(),var-1) * get_next_prime();

    return ret_val;   
}

main()

{
    unsigned long int    var     = 0;
    char                 buf[50] = {'\0'};
    unsigned long int    output  = 0;

    while(fgets(buf,sizeof(buf),stdin)){
        sscanf(buf,"%lu",&var);
        output = cal_improbability(var);
        printf("%lu %lu\n",var,output);

        reset_prime_index();
    }
}
