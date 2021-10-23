-- adding the types of whitelisting
INSERT INTO WHITELIST_TYPE(ID, `TYPE`, DESCRIPTION) VALUES (1, 'group', 'The user can call the bot if whitelisting is enabled and part of the chosen group.');

-- define the types of configs that can be enabled
INSERT INTO CONFIGS(ID, CONFIG) VALUES (1, 'whitelisting');
INSERT INTO CONFIGS(ID, CONFIG) VALUES (2, 'access-denied');

-- adding my default server to debug
INSERT INTO GUILDS(ID, `NAME`)  VALUES (1, 'I don''t take betrayal lightly');

-- adding default one of my group in order to test
INSERT INTO WHITELISTS(ID, GUILD, `TYPE`, `GROUP`) VALUES (1, 1, 1, 'wow nerd');

-- putting values in the configs
INSERT INTO GUILD_CONFIGURATION(ID, CONFIG, ENABLED, GUILD, EXTRA) VALUES  (1, 2, TRUE, 1, 'Fuck out of here boy, you don''t have the right to do this.');