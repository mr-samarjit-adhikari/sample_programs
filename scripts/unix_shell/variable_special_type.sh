#! /bin/bash

# Spacial variable Types
# 1. Local Variable
# 2. Environmental variable
# Note on 2.
# Space alloted to environment variable is limited. Creating too many variables
# may cause problem

#eval="`seq 100000000 | sed -e 's/.*/export var&=aaaaaaaaaaaaaaaaaaaaaaaaaa/'`"

# 3. Positional parameter
# Arguments passed to the script from command line 
# as $0,$1,$2,$3,....
# $0 - name of the script
# $1 - 1st argument
# $2 - 2nd Argument
# ...
# $9 - 9th arguments
# ${10} - 10th Arguments
# ${11} - 11th Arguments....
# $* , $@ denotes all positional parameters
