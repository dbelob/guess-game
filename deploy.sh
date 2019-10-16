#!/bin/sh
set -x
openssl enc -a -d -aes-256-cbc -in deploy_rsa.enc -out deploy_rsa -pass env:DECYPHER_KEY
chmod 400 deploy_rsa
scp -i deploy_rsa -o "StrictHostKeyChecking no" guess-game-server/target/guess-game.war root@jugspeakers.online:/opt/guess/guess-game-${TRAVIS_BUILD_NUMBER}.war

ssh -i deploy_rsa root@jugspeakers.online "chmod 755 /opt/guess/guess-game-${TRAVIS_BUILD_NUMBER}.war && service guess-game stop && ln -sf /opt/guess/guess-game-${TRAVIS_BUILD_NUMBER}.war /etc/init.d/guess-game && systemctl daemon-reload && service guess-game start"

rm -f deploy_rsa
