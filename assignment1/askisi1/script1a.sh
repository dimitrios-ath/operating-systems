#!/bin/bash

job(){
    line=`echo $1 | xargs`
    md5=`curl -L $1 2>/dev/null`
    if [[ $? == 0 ]]; then
        md5=`echo "$md5" | md5sum | head -c 32`
        if grep -q $line .pages.txt; then
            if [[ "$line:$md5" != `grep -s "$line" ./.pages.txt` ]]; then
                echo $line
            fi
        else
            echo "$line INIT"
        fi
        echo "$line:$md5" >> ./.pages_temp.txt
    else
        echo "$line FAILED" >> /proc/self/fd/2
    fi
}
export -f job

touch ./.pages.txt
for line in $(grep -v '^#' $1)
do
    job $line
done
mv ./.pages_temp.txt ./.pages.txt 2>/dev/null