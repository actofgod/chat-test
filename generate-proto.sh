#!/bin/bash

protoc -I=resources/proto --java_out=src/main/java resources/proto/auth_request.proto
protoc -I=resources/proto --java_out=src/main/java resources/proto/auth_response.proto
protoc -I=resources/proto --java_out=src/main/java resources/proto/error_info.proto
protoc -I=resources/proto --java_out=src/main/java resources/proto/message_request.proto
protoc -I=resources/proto --java_out=src/main/java resources/proto/message_response.proto
protoc -I=resources/proto --java_out=src/main/java resources/proto/request.proto
protoc -I=resources/proto --java_out=src/main/java resources/proto/response.proto
protoc -I=resources/proto --java_out=src/main/java resources/proto/user_request.proto
protoc -I=resources/proto --java_out=src/main/java resources/proto/user_response.proto
