#!/bin/bash
# Call this script with at least 10 parameters, for example
# ./scriptname 1 2 3 4 5 6 7 8 9 10
MINPARAMS=10
#prints all parameters 
arg_count=$#
if [ "$arg_count" -lt 1 ];then 
    echo "This script needs at least $MINPARAMS params.";
    exit 0;
fi

args=$1
while [ ! -z "$args" ];
do
    echo "param '#$1' is $args"
    shift
    args=$1
done

exit 0

#N.B.
#args=$#
# Number of args passed.
#lastarg=${!args}
# Note: This is an *indirect reference* to $args ...
# Check in function example 
