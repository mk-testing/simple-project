#!/bin/bash

# Update BUILD_TIMESTAMP to force image recreation
now=$(date)
sed "s/<BUILD_TIMESTAMP>/$now/" Dockerfile_build > .Dockerfile_build

# Build source code
docker build -t simple-service -f .Dockerfile_build .
