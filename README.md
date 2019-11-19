[![Build Status](https://travis-ci.org/jugrugroup/guess-game.svg?branch=master)](https://travis-ci.org/jugrugroup/guess-game)
[![Dependabot Status](https://badgen.net/dependabot/JugruGroup/guess-game)](https://dependabot.com)

# Guess Game

![Screenshot](/images/screenshot.png)

Guess name by picture, picture by name, talk by speaker or speaker by talk.

## Running

### Online

https://jugspeakers.online

### Offline

1. Install [Java SE 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html) or higher (*JRE* or *JDK*) 

1. Extract files from ZIP, for example:

    `unzip guess-game-<version>.zip`
    
1. Change directory:

    `cd guess-game-<version>`

1. Run:

    `runme.bat` (*Windows*)  
    `runme.sh` (*macOS* or *Linux*)

1. Access the running web application at:

    http://localhost:8080

## Download

1. Open [Releases](https://github.com/dbelob/guess-game/releases) page

1. Choose latest version

1. Download `guess-game-<version>.zip` file

## Compilation

1. Install [Java SE 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html) or higher (*JDK*) 

1. Install [Apache Maven 3.5.0](https://maven.apache.org/download.cgi) or higher 

1. From the command line with *Maven* (in the root directory):

    `mvn clean package -DskipTests`
    
1. Change directory:

    `cd guess-game-distrib/target`

1. Find distribution file:

    `guess-game-<version>.zip`
