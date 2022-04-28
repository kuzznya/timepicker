#!/bin/bash

# Sometimes needed, don't know why

kubectl -n kube-system scale deploy traefik --replicas 0
kubectl -n kube-system scale deploy traefik --replicas 1
