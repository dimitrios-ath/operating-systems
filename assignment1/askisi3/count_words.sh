if [ $# != 2 ] || [ $1 = "-h" ] || [ $1 = "--help" ]; then
    printf "Usage: $0 [options] file n\n\nDescription: Print the n most frequent words inside the given text file in descending order\n\nOptions:\n   -h, --help\tPrint help message.\n"; exit 0; fi
sed -nE '/\*\*\*\ START\ OF\ THIS|THE\ PROJECT\ GUTENBERG\ EBOOK\ /,/\*\*\*\ END\ OF\ THIS|THE\ PROJECT\ GUTENBERG\ EBOOK\ /p' $1 | head -n -1 | tail -n +2 | tr -d '\n' | tr -c '[:graph:]' '[\n*]' | sed "s/'.//" | tr -c '[:alnum:]' '[\n*]' | sed '/^[[:space:]]*$/d' | sort | uniq -ci | sort -nr | head -n $2 | sed "s/\(.*\) \(.*\)/\2\1/" | tr -s " "