#!/bin/bash
# func-cmdlinearg.sh
# Call this script with a command-line argument,
#+ something like $0 arg1.

#Passing an indirect reference to function
function function_demo ()
{
echo "$1"
}

var="String1";
String1=String2;

function_demo ${var}  #print String1
function_demo ${!var} #print String2

#Dereferencing a parameter passed to function

function derefence(){
    var=\$"$1"  #name of the variable not value
    
    echo $var   #$Junk
    x=`eval "expr \"$var\" "`

    echo "value of \$x is \" $x \" "

    echo $1=$x   
    eval "$1=\"Some different text\"" 
}

Junk="Some text"
echo $Junk "before"   # Some text before

derefence Junk
echo $Junk "After"  #Modified text after

function return_val(){
    echo $1
#    return $1
}

ret=$(return_val 0)
#echo "return value from function $?"
echo "return value from function ver2 $ret"

ret=$(return_val 255)
#echo "return value from function $?"
echo "return value from function ver2 $ret"

ret=$(return_val 258)
#echo "return value from function $?"
echo "return value from function ver2 $ret"

ret=$(return_val "StringValue")
#echo "return value from function $ret"
echo "return value from function ver2 $ret"
