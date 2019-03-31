# Prototype for stream join algorithms

This repo contains:
 - `hu.bme.vzqixx.punctuations` Java package to experiment with punctuated streams
    - `InsertPunctuation` insert punctuation to a stream represented in a file  
      Usage: `InsertPunctuation punctuationMode punctuationOnFieldPosition path inputFile outputFile`, where `punctuationMode` can be: `NONE`, `TAIL`, `HEAD_AND_TAIL`

## Compilation

Pending to add build tool. Until then, use the following commands under bash 4 from the git root:

```shell
mkdir -p out && shopt -s globstar && javac -d out/ src/**/*.java

```
