#! /bin/bash
set -aux

# An if/then construct tests whether the exit status 
# of a list of commands is 0 (since 0 means "success"
# by UNIX convention), and if so, executes one or more commands.

# There exists a dedicated command called 
# [ (left bracket special character). It is a synonym for test,
# and a builtin for efficiency reasons. This command considers 
# its arguments as comparison expressions
# or file tests and returns an exit status corresponding 
# to the result of the comparison (0 for true, 1 for
# false).

#syntax 

#if [ condition-true ]
#then
#command 1
#command 2
#...
#else # Or else ...
## Adds default code block executing if original condition tests false.
#command 3
#command 4
#...
#fi

# ((expression))
#    The  expression  is  evaluated  according to  
#    ARITHMETIC  EVALUATION.   
#    If  the  value of the expression is non-
#    zero, the return  status  is  0;  otherwise  the
#    return  status is 1.  This is exactly equivalent
#    to let "expression".
#
# [[ expression ]]
#    Return a status of 0 or 1 depending on the eval‐
#    uation of the conditional expression expression.
# e.g.

(( 0 && 1 ))   #Logical AND
echo $?        #print 1

# Alternate way of writing the above expr 
let "num = (( 0 && 1 ))"
echo $num   # print 0
let "num = (( 0 && 1 ))"
echo $?     # print 1

(( 200 || 11 ))  #Logical OR
echo $?          #print 0

#Alternate way of writing the above expr 
let "num = (( 200 || 11 ))"
echo $num    # 1
let "num = (( 200 || 11 ))"
echo $?      # 0

(( 200 | 11 ))   #Bitwise OR
echo $?		 #0

let "num = (( 200 | 11 ))"   #??#
echo $num 
let "num = (( 200 | 11 ))"   #??#
echo $? 

#if ..else check

echo "Testing \"0\""
if [ 0 ]; then
    echo "0 is true."
else
# Or else ...
    echo "0 is false."
fi
# 0 is true.
echo

echo "Testing \"1\""
if [ 1 ]
# one
then
    echo "1 is true."
else
    echo "1 is false."
fi
# 1 is true.
echo

[[ 1 -lt 0 ]]
echo $?      #print 1 as above condition is false, 0 is success

#########
# For more information on test 
# info test
#########
