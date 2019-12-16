#!/bin/bash

#!/bin/bash
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
echo "Local images:"
docker images
docker push $DOCKER_USERNAME/simple-service:latest