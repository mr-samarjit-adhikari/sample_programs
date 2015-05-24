#! /bin/bash

# Demonstrate variable substitution

variable1=23

echo variable1     	#prints varaible1
echo $variable1 	#prints 23

#Quoting
# '' is used to stop expanding the string by shell
# but could be expanded by calling program
# "" is used to stop expanding the string except
# $,`,\ by shell.

echo '$variable1'  #prints $variable1
echo "$variable1"  #expand by shell and prints 23

echo "Hello world student?"  #all words with spcaes was treated as single world
echo "Value of variable1 is $variable1"

#echoing Weird variables
echo -n "Before Newline "  #Do not echo new line in echo
echo $'\n'		  #print newline
echo "After newline"
echo "$'\n'" 		  # ??print??

#parameter substitution
# ${parameter}  		-->Same as $parameter
# ${parametr-default}		-->if parameter not set use default
# ${parameter=default}		-->if parameter not set, set it to default
# ${parameter+alt_value} 	-->if parameter is set use alt_value else null
#e.g.1
var1=1
var2=2
# var3 is unset.
echo ${var1-$var2} 		#??print ?
echo ${var3-$var2}		#??print??
echo ${username-`whoami`}	#print <myusername>

echo "value of var3 = $var3"
echo ${var3=$var2}
echo "After value of var3 = $var3"

var1_alt=100
echo ${var1+$var1_alt}  #print 100
#quick test??
#echo ${var1+var1_alt}  #??print??

# = 	the assignment operator (no space before and after)
