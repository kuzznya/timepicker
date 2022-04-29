#!/bin/bash

kubectl create secret generic secrets --from-env-file .env
