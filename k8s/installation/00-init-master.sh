#!/bin/bash

set -e

source common.sh

MASTER_IP=$(get_ip "$MASTER_HOST")

INSTALL_K3S_EXEC="server --node-ip $MASTER_IP --node-external-ip $MASTER_IP --tls-san $MASTER_IP"

# shellcheck disable=SC2029
ssh "$MASTER_SSH" "curl -sfL https://get.k3s.io | INSTALL_K3S_EXEC='${INSTALL_K3S_EXEC}' sh -"
scp "${MASTER_SSH}:/etc/rancher/k3s/k3s.yaml" .
mv k3s.yaml ~/.kube/k3s-vps0-kuzznya-config
# sed -i "s/127.0.0.1/$MASTER_IP/" ~/.kube/k3s-vps0-kuzznya-config   # not working on Mac
sed -i '' -e "s/127.0.0.1/$MASTER_IP/" ~/.kube/k3s-vps0-kuzznya-config
echo "Please check that 127.0.0.1 is replaced to $MASTER_IP in ~/.kube/k3s-vps0-kuzznya-config"
# shellcheck disable=SC2016
echo 'Add "export KUBECONFIG=$HOME/.kube/k3s-vps0-kuzznya-config" to .profile manually'

envsubst < registries.template.yaml > registries.yaml
scp registries.yaml "${MASTER_SSH}:/etc/rancher/k3s/"
rm -rf registries.yaml
ssh "$MASTER_SSH" 'service k3s restart'
