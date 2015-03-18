# checkers-detector

Checkers pieces placement detection from a single image

## Dependencies

- Java compiler (javac) and Virtual machine
- imagej

## How to build

```shell
# Setup classpath
export CLASSPATH=/usr/share/java/ij.jar:plugins/checkers-detector:.
# Compile files
javac plugins/checkers-detector/*.java
```

## How to run

```shell
# Launch imagej with the file img/dames.png
java ij.ImageJ img/dames.png
```
Then use the `plugins > checkers-detector` panel to use the filters
