#!/bin/bash

echo "NAMESPACE: $1"
echo "LATEST VERSION: $5"


oc process -f /home/runner/work/jag-efax/jag-efax/openshift/templates/efax-webservice/dc.yaml -n $1 \
    -p APPLICATION_NAME=$2 \
    -p LICENSE_PLATE=$3 \
    -p ENVIRONMENT=$4 \
    -p EFAX_IMAGE_VERSION=$5 | \
    oc apply -f -
