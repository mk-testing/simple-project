#!/bin/bash

# Build source code
docker build -t simple-service -f Dockerfile_build .

# Tag image to push on DockerHub repository
docker tag simple-service mk8testing/simple-service