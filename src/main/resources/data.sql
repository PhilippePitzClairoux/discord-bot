CREATE TABLE guilds
(
    id   AUTO_INCREMENT INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE whitelist_type
(
    id   INTEGER      NOT NULL PRIMARY KEY,
    type VARCHAR(255) NOT NULL
);

CREATE TABLE configs
(
    id   INTEGER      NOT NULL PRIMARY KEY,
    config VARCHAR(255) NOT NULL
);


CREATE TABLE guild_configuration (
    id AUTO_INCREMENT INTEGER NOT NULL PRIMARY KEY,
    guild INTEGER NOT NULL,
    config INTEGER NOT NULL,
    enabled BOOLEAN NOT NULL,
    FOREIGN KEY(guild) REFERENCES guilds(id),
    FOREIGN KEY(config) REFERENCES configs(id)
);

CREATE TABLE whitelists
(
    id       AUTO_INCREMENT INTEGER NOT NULL PRIMARY KEY,
    username VARCHAR(255)           NOT NULL,
    guild    INTEGER                NOT NULL,
    type     INTEGER                NOT NULL,
    FOREIGN KEY (guild) REFERENCES guilds (id),
    FOREIGN KEY (type) REFERENCES whitelist_type (id)
);

INSERT INTO whitelist_type(id, type)
VALUES (1, 'user');

INSERT INTO configs(id, config) VALUES (1, 'whitelisting');
