#!/usr/bin/env bash

KAFKA_CONNECT_REDIS_ZIP_URL=https://d1i4a15mxbxib1.cloudfront.net/api/plugins/jcustenborder/kafka-connect-redis/versions/0.0.4/jcustenborder-kafka-connect-redis-0.0.4.zip

mkdir -p connect/redis-connector

cd ./connect/redis-connector && \
    wget $KAFKA_CONNECT_REDIS_ZIP_URL && unzip jcustenborder-kafka-connect-redis-0.0.4.zip
