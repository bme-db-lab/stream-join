#!/bin/bash

CSV_PATH=$HOME/git/graphs/social_network_sf
MODE=${1:-HEAD_AND_TAIL}
INDEX=${2:-8} # zero-based index of the column for the punctuation pattern

MAIN_CLASS=hu.bme.vzqixx.punctuations.InsertPunctuation

cd $(dirname $0)/../out

for sf in 0.1 0.3 1 3 ; do
	echo $sf
	time java $MAIN_CLASS $MODE $INDEX ${CSV_PATH}${sf} message_0_0-postgres.csv message-punctuated-${MODE}.csv
done
