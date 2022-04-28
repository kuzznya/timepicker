#!/bin/bash

if [[ -n "$CASSANDRA_KEYSPACE" ]] ; then
    CQL="CREATE KEYSPACE $CASSANDRA_KEYSPACE IF NOT EXISTS WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1};"
    i=0
    until echo "$CQL" | cqlsh; do
        echo "Cassandra is unavailable - retrying"
        ((i++))
        [[ $i -eq 1000 ]] && exit 1
        sleep 2
    done &
fi
