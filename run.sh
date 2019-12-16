#!/bin/bash

# Bild source code
docker build -t simple-service-run -f Dockerfile_run .
docker run -d simple-service-run