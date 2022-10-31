#!/bin/bash

set -e

kubectl create namespace timepicker || echo "Cannot create namespace 'timepicker', skipping"
./create-secrets.sh
./create-cassandra-configs.sh
kubectl apply -f configs/ --namespace timepicker
