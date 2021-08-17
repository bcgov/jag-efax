#!/bin/bash

oc process -f openshift/templates/efax-webservice/dc.yaml -n $DEPLOY_NAMESPACE \
    -p APPNAME=$APPLICATION_NAME
    -p LICENSE_PLATE=$LICENSE_PLATE
    -p ENVIRONMENT=$ENVIRONMENT
