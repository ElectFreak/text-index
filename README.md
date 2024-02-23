# Indexing a text file

[Task Description](./TASK.md)

## Scheme for json index of text file

```json
  {
  "wordsInfo": {
    "36985": [
      {
        "pageNumber": 1,
        "wordForm": "лев",
        "lineNumber": 0
      },
      {
        "pageNumber": 35,
        "wordForm": "левую",
        "lineNumber": 1547
      },
      {
        "pageNumber": 50,
        "wordForm": "левой",
        "lineNumber": 2210
      },
      {
        "pageNumber": 50,
        "wordForm": "левую",
        "lineNumber": 2215
      }
    ],
  }
}
```

`pageNumber` - number of the page on which the word was encountered

`wordForm` - the form in which the word was encountered

`lineNumber` - on which line in the source text (excluding empty lines) the word was encountered.

That is, the number of the word in the dictionary is matched with an array with an object for each case of meeting this word in the text.

## Run Examples:

### Run to unpack the dictionary:
```bash
./dict.sh
```

### Index compilation:
```bash
./gradlew run --args="create-index --text-path='data/Childhood.txt' --text-index-path='data/index.json'" 
```

### Find all occurrences:
```bash
./gradlew run --args="find-all --text-path='data/Childhood.txt' --text-index-path='data/index.json' --word='лес'"
```

### Output information based on the index:
```bash
./gradlew run --args="stats-from-index --text-index-path='data/index.json' --word='лес' --number=10 --from-group='мебель'"    
```