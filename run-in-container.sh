#!/bin/bash

# запускаем сервисы
service redis-server start
service postgresql start

# ждем, пока запуститься база данных
sleep 15s

# запускаем чат
java -jar target/chat-server.jar
