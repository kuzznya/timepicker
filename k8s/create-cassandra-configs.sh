#!/bin/bash

set -e

kubectl delete configmap cassandra-configs --namespace timepicker || echo "Cannot delete configmap 'cassandra-configs', skipping"
kubectl create configmap cassandra-configs --from-file ../cassandra --namespace timepicker
