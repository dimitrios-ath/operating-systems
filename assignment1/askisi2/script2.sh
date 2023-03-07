mkdir ./.extracted 2>/dev/null && tar -xf $1 -C ./.extracted 2>/dev/null
rm -rf ./assignments 2>/dev/null && mkdir ./assignments 2>/dev/null
for file in $(find ./.extracted/ -name "*.txt"); do
    url=`grep -v "^#" $file | grep "https" | head -n 1`
    GIT_TERMINAL_PROMPT=0 git -C ./assignments clone -q $url 2>/dev/null
    if [ $? -eq 0 ]; then
        printf "$url: Cloning OK\n"
    else
        printf "$url: Cloning FAILED\n" >> /proc/self/fd/2
    fi
done
for repo in `ls ./assignments`; do
    num_folders=`find ./assignments/$repo -mindepth 1 -type d -not -path '*/\.git/*' | grep -v '/\.git$' | wc -l`
    num_txts=`find ./assignments/$repo -mindepth 1 -type f -not -path '*/\.git/*' -name "*.txt" | wc -l`
    num_others=`find ./assignments/$repo -mindepth 1 -type f -not -path '*/\.git/*' -not -name "*.txt" | wc -l`
    printf "$repo:\nNumber of directories: $num_folders\nNumber of txt files: $num_txts\nNumber of other files: $num_others\n"
    [ -d "./assignments/$repo/more" ] && [ -f "./assignments/$repo/dataA.txt" ] && [ -f "./assignments/$repo/more/dataB.txt" ] && [ -f "./assignments/$repo/more/dataC.txt" ] && [ $num_folders -eq 1 ] && [ $num_txts -eq 3 ] && [ $num_others -eq 0 ] && printf "Directory structure is OK.\n" || printf "Directory structure is NOT OK.\n" >> /proc/self/fd/2
done
rm -rf ./.extracted 2>/dev/null
