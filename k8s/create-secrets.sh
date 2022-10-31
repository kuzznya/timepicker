#!/bin/bash

set -e

kubectl delete secret secrets --namespace timepicker || echo "Cannot delete secret 'secrets', skipping"
kubectl create secret generic secrets --from-env-file .env --namespace timepicker
