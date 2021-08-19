#!/bin/bash

oc rollout status -n $NAMESPACE dc/$APPLICATION_NAME --watch
