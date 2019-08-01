# Guess Game

Guess name by picture or picture by name 

## Requirements

* [JDK 8+](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Apache Maven 3.5.0+](https://maven.apache.org/download.cgi)

## Compilation

1. From the command line with *Maven* (in the root directory):

    `mvn clean package -Dmaven.test.skip=true`
    
1. Change directory:

    `cd guess-game-distrib/target`

1. Find distribution file:

    `guess-game-<version>.zip`

## Running

1. Install *Java SE 8* or higher (*JRE* or *JDK*) 

1. Extract files from ZIP, for example:

    `unzip guess-game-<version>.zip`
    
1. Change directory:

    `cd guess-game-<version>`

1. Run:

    `runme.bat` (*Windows*)  
    `runme.sh` (*macOS* or *Linux*)

1. Access the running web application at:

    http://localhost:8080
