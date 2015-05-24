package com.hp.ov.nmc.codewars;

import java.util.List;

public class Main {

    public void doRun(){



    }

    public Integer[] findAllCombinationsOfNIntoThreeBinsOptimized(int N){    //At-least one in each bin
        for(int n=1 ; n < N ; n++){
            for(int m=3 ; m < (N-n) ; m = m + 3){
                int o = N - n - m;
                //ADd to list OR Directly check here.
                if((n*4 + m*0.5 + o*0.25) == N){
                    Integer[] solution = new Integer[3];
                    solution[0] = n;
                    solution[1] = m;
                    solution[2] = o;
                    return solution;
                }
            }
        }
        return new Integer[0];
    }

    public Integer[] findAllCombinationsOfNIntoThreeBins(int N){    //At-least one in each bin
        for(int n=1 ; n < N ; n++){
            for(int m=1 ; m < (N-n) ; m++){
                int o = N - n - m;
                //ADd to list OR Directly check here.
                if((n*4 + m*0.5 + o*0.25) == N){
                    Integer[] solution = new Integer[3];
                    solution[0] = n;
                    solution[1] = m;
                    solution[2] = o;
                    return solution;
                }
            }
        }
        return new Integer[0];
    }

    public static void main(String[] args) {
        Main obj = new Main();
        obj.doRun();
    }
}
