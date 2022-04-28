CREATE KEYSPACE IF NOT EXISTS events WITH replication = {'class':'SimpleStrategy','replication_factor':'1'};

CREATE TABLE IF NOT EXISTS events.event(
    participant text,
    event_id uuid,
    title text,
    author text,
    PRIMARY KEY ( (participant, event_id) )
);
