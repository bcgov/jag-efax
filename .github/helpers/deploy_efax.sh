#!/bin/bash

# dependancies
# - oc

# environment variables
# DEPLOY_NAMESPACE
# APPLICATION_NAME
# LICENSE_PLATE
# ENVIRONMENT

oc process -f openshift/templates/efax-webservice/dc.yaml -n $DEPLOY_NAMESPACE \
    -p APPNAME=$APPLICATION_NAME
    -p LICENSE_PLATE=$LICENSE_PLATE
    -p ENVIRONMENT=$ENVIRONMENT
