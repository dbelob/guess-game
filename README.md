# Guess Game

Guess name by picture or picture by name 

## Requirements

* [JDK 8+](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Apache Maven 3.5.0+](https://maven.apache.org/download.cgi)
* [Node.js 10.16.0](https://nodejs.org) (optional)

## Running

### Method 1

1. From the command line with *Maven* (in the root directory):

    `mvn clean package -Dmaven.test.skip=true`

1. Change directory:

    `cd guess-game-server`

1. From the command line:

    `java -jar target/guess-game.war`

1. Access the deployed web application at:

    http://localhost:8080

### Method 2

1. From the command line with *Maven* (in the root directory):

    `mvn clean install -Dmaven.test.skip=true`

1. Change directory:

    `cd guess-game-server`

1. From the command line with *Maven*:

    `mvn spring-boot:run`

1. Access the deployed web application at:

    http://localhost:8080

### Method 3

1. Install *Node.js*

1. Run to ensure that *npm* is working:

    `npm -v`

1. Change directory:

    `cd guess-game-server`

1. From the command line with *Maven*:

    `mvn spring-boot:run`  

1. Change directory:

    `cd guess-game-web`

1. From the command line with *npm*:

    `npm start`

1. Access the deployed web application at:

    http://localhost:4200

