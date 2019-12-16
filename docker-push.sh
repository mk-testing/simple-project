#!/bin/bash

#!/bin/bash
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
docker push mk8testing/simple-project:tagname