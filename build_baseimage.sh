#!/bin/bash

# Update BUILD_TIMESTAMP to force image recreation
now=$(date)
sed "s/<BUILD_TIMESTAMP>/$now/" Dockerfile_platform > .Dockerfile_platform

# Create Base Image
docker build -t simple-service-baseimage -f .Dockerfile_platform .