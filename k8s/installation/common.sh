#!/bin/bash

# get_ip <host>
get_ip() {
  nslookup $1 | grep Address | awk '{print $2}' | grep --invert-match 192.168 | grep --invert-match 172.20 | grep --invert-match 8.8.8
}

export MASTER_HOST=vps0.kuzznya.space
export MASTER_USERNAME=root
export MASTER_SSH="${MASTER_USERNAME}@${MASTER_HOST}"
