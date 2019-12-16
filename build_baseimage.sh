#!/bin/bash

# Create Base Image
docker build -t simple-service-baseimage -f Dockerfile_platform .