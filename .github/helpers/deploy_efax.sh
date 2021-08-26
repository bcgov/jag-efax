#!/bin/bash

# script to process efax-webservice deployment config template and deploy it to a given namespace

echo "VERSION BEING DEPLOYED: $1"

oc process -f /home/runner/work/jag-efax/jag-efax/openshift/templates/efax-webservice/dc.yaml -n $NAMESPACE \
    -p APPLICATION_NAME=$APPLICATION_NAME \
    -p LICENSE_PLATE=$LICENSE_PLATE \
    -p ENVIRONMENT=$ENVIRONMENT \
    -p EFAX_IMAGE_VERSION=$1 | \
    oc apply -f -
