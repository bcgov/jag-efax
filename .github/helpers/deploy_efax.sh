#!/bin/bash

oc process -f dc.yaml -n ${{ secrets.OPENSHIFT_DEV_NAMESPACE }} \
    -p APPLICATION_NAME=${{ secrets.APPLICATION_NAME }} \
    -p LICENSE_PLATE=${{ secrets.LICENSE_PLATE }} \
    -p ENVIRONMENT=${{ secrets.DEV_ENVIRONMENT }} \
    -p EFAX_IMAGE_VERSION=${env.LATEST_VERSION}| \
    oc4 apply -f -
