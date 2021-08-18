#!/bin/bash

echo "LATEST VERSION: $1"

oc process -f /home/runner/work/jag-efax/jag-efax/openshift/templates/efax-webservice/dc.yaml -n $NAMESPACE \
    -p APPLICATION_NAME=$APPLICATION_NAME \
    -p LICENSE_PLATE=$LICENSE_PLATE \
    -p ENVIRONMENT=$DEV_ENVIRONMENT \
    -p EFAX_IMAGE_VERSION=$1 | \
    oc apply -f -
