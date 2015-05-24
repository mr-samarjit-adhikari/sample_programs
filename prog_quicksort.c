#include <stdio.h>

 void quicksort(int x[], int first, int last) {
     int pivIndex = 0;
     if(first < last) {
         pivIndex = partition(x,first, last);
         quicksort(x,first,(pivIndex-1));
         quicksort(x,(pivIndex+1),last);
     }
 }
 
 int partition(int y[], int f, int l) {
     int up,down,temp;
     int piv = y[f];
     up = f;
     down = l;
     goto partLS;
     do { 
         temp = y[up];
         y[up] = y[down];
         y[down] = temp;
     partLS:
         while (y[up] <= piv && up < l) {
             up++;
         }
         while (y[down] > piv  && down > f ) {
             down--;
         }
     } while (down > up);
     y[f] = y[down];
     y[down] = piv;
     return down;
 }

main(){
    int input[] = {40,20,10,80,60,50,7,30,100};
    int input_size = sizeof(input)/sizeof(int);
    int index = 0;
    quicksort(input,0,8);

    for(index=0;index<input_size;index++){
        printf("%d ",input[index]);
    }
    printf("\n");
}
