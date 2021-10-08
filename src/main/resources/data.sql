INSERT INTO WHITELIST_TYPE(ID, TYPE, DESCRIPTION) VALUES (1, 'user', 'The user can call the bot if whitelisting is enabled.');
INSERT INTO CONFIGS(ID, CONFIG) VALUES (1, 'whitelisting');
INSERT INTO CONFIGS(ID, CONFIG) VALUES (2, 'access-denied');
INSERT INTO GUILDS(ID, NAME)  VALUES (1, 'I don''t take betrayal lightly');
INSERT INTO WHITELISTS(ID, GUILD, TYPE, USERNAME) VALUES (1, 1, 1, '297195342540046337');
INSERT INTO GUILD_CONFIGURATION(ID, CONFIG, ENABLED, GUILD, EXTRA) VALUES (1, 2, TRUE, 1, 'Fuck out of here boy, you don''t have the right to do this.');