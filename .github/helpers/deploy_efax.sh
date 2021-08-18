#!/bin/bash

oc process -f dc.yaml -n $1 \
    -p APPLICATION_NAME=$2 \
    -p LICENSE_PLATE=$3 \
    -p ENVIRONMENT=$4 \
    -p EFAX_IMAGE_VERSION=$5 | \
    oc apply -f -
