-- define the types of configs that can be enabled
INSERT INTO CONFIGS(CONFIG) VALUES ('whitelisting');
INSERT INTO CONFIGS(CONFIG) VALUES ('access-denied');

-- adding my default server to debug
INSERT INTO GUILDS(`NAME`)  VALUES ('I don''t take betrayal lightly');

-- adding default one of my group in order to test
INSERT INTO WHITELISTS(GUILD, GROUP_NAME) VALUES (1, 'wow nerd');

-- putting values in the configs
INSERT INTO GUILD_CONFIGURATION(CONFIG, ENABLED, GUILD) VALUES  (2, TRUE, 1);
INSERT INTO GUILD_CONFIGURATION(CONFIG, ENABLED, GUILD, EXTRA) VALUES  (1, TRUE, 1, 'Where the group at tho brother...');