#!/bin/sh
openssl enc -d -aes-256-cbc -in deploy_rsa.enc -out deploy_rsa -pass env:DECYPHER_KEY
scp -i deploy_rsa -o "StrictHostKeyChecking no" guess-game-server/target/guess-game.war root@jugspeakers.online:/opt/guess/guess-game-${TRAVIS_BUILD_NUMBER}.war
