#! /bin/bash

#----- syntax -----
#for arg in [list]
#do
#command(s)...
#done

for arg in "item1" "item2" "item3"
do
    echo "arg is $arg"
done

for file in `ls`
do
    echo "-- file is -- $file"
done

#Another form of for loop
for((i=0;i<10;i++))
do
    echo "value of i is $i"
done

# while loop --- syntax ---

#while [ condition ]
#do
#command(s)...
#done

count=0;
while [ $count -lt 10 ]
do
    echo "-- while -- count = $count";
    let "count = count + 1"
done

#Another form of while loop
count=0;
while let "count < 10"
do
    echo "-- while(2) -- count = $count";
    count=$((count + 1))
done

#Other form of while loop C style ??
count=0;
while ((count<10)) 
do
    echo "-- while(3 C-style ) -- count = $count";
    count=$((count + 1))
done

# until -- syntax ---
# This is opposite to while
#until [ condition-is-false ]
#do
#command(s)...
#done

LIMIT=10
var=0
until (( var > LIMIT ))
do 
    echo -n "$var "
    (( var++ ))
done
echo
