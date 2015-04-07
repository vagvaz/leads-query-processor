#!/bin/bash
screen -wipe
rawscreens=`screen -ls | grep shell | awk '{print $1}' `
#echo $rawscreens
#a=( $rawscreens )
#echo ${a[0]}

i=1
for word in $rawscreens; 
do 
	Array=`echo $word | sed -e 's/-/\n/g'`
	
	title=''
	for a in $Array;
	do
		title=$a;
		beforedot=${a%.*}
		title=${a#*.}
		title=${a#*_}
		echo $title;
		break;
	done
	echo $word' '$title; 
	comm="screen -r "$word
	echo $comm"newtab$i"
	guake -n "newtab$i" -e "$comm";
	sleep 0.1
	guake -r "$title"
	sleep 0.7
	i=$((i+1))

done

#tr@tr-OptiPlex-390:~$ nano screens.txt 
#tr@tr-OptiPlex-390:~$ read work _ < screens.txt 
#tr@tr-OptiPlex-390:~$ echo $work
#16849.pts-17.tr-OptiPlex-390
#tr@tr-OptiPlex-390:~$ while read work _; guake -n "screen -r "$word -r "1" -e $word 



#vertx runMod gr.tuc.softnet~log-sink-module~1.0-SNAPSHOT -conf /tmp/conf/log-sink-conf.json  -ha 
#vertx runMod gr.tuc.softnet~processor-webservice~1.0-SNAPSHOT -conf /tmp/Rprocessor-webservice-default-01d81d2b-2427-4b0f-aeb3-8f80e6303406.json -ha
#vertx runMod gr.tuc.softnet~log-sink-module~1.0-SNAPSHOT -conf /tmp/conf/log-sink-conf.json
