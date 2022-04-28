CREATE KEYSPACE IF NOT EXISTS events WITH replication = {'class':'SimpleStrategy','replication_factor':'1'};
DROP TABLE event;
CREATE TABLE IF NOT EXISTS events.event(
    id uuid,
    participant text,
    title text,
    author text,
    PRIMARY KEY ( (id, participant) )
);
