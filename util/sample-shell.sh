#!/bin/sh

##
## Sample shell action in progress
##

echo "Starting my shell action"

theDate=`date`

echo "The current time is ${theDate}"

input=$1

echo "You had told us: ${input}"

echo "Bye."
