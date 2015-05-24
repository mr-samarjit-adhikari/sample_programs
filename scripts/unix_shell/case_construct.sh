# /bin/bash

# How to use case 
# This is switch case in Shell
# e.g. 
# ---- syntax ----
#case "$variable" in
#"$condition1" )
#command...
#;;
#"$condition2" )
#command...
#;;
#esac

# checking command line using case
while [ $# -gt 0 ]; do
    case "$1" in
        -d|--debug)       # "-d" or "--debug" parameter?
            echo "DEBUG mode is ON"
            DEBUG=1 ;;
        -c|--conf)
            CONFFILE="$2"
            shift
            if [ ! -f $CONFFILE ]; then
                echo "Error: Supplied file doesn't exist!"
                exit $E_CONFFILE
                # File not found error.
            fi
         ;;
    esac
shift
# Check next set of parameters.
done
