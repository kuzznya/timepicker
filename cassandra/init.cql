CREATE KEYSPACE IF NOT EXISTS events WITH replication = {'class':'SimpleStrategy','replication_factor':'1'};

CREATE TABLE IF NOT EXISTS events.event(
    id uuid,
    participant text,
    title text,
    author text,
    PRIMARY KEY ( (id, participant) )
);

CREATE KEYSPACE IF NOT EXISTS votes WITH replication = {'class':'SimpleStrategy','replication_factor':'1'};

DROP TABLE votes.vote;
CREATE TABLE IF NOT EXISTS votes.vote(
    event_id uuid,
    username text,
    date date,
    PRIMARY KEY ( (event_id, username, date) )
);
