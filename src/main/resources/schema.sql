CREATE TABLE IF NOT EXISTS permissions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_snowflake CHAR(24) NOT NULL,
    guild_snowflake CHAR(24),
    permission CHAR(50) NOT NULL,
    UNIQUE(user_snowflake, guild_snowflake, permission)
);

CREATE TABLE IF NOT EXISTS country (
    id CHAR(2) PRIMARY KEY,
    name CHAR(60) UNIQUE
);

CREATE TABLE IF NOT EXISTS state (
    id CHAR(3),
    country_id CHAR(2),
    name CHAR(60) NOT NULL,
    PRIMARY KEY (id, country_id),
    FOREIGN KEY (country_id)
        REFERENCES country (id)
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    UNIQUE(country_id, name)
);

CREATE TABLE IF NOT EXISTS city (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    country_id CHAR(2),
    sp_id CHAR(3),
    name CHAR(60) NOT NULL,
    latitude REAL NOT NULL,
    longitude REAL NOT NULL,
    FOREIGN KEY (country_id)
        REFERENCES country (id)
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    FOREIGN KEY (country_id, sp_id)
        REFERENCES state (country_id, id)
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    UNIQUE(country_id, sp_id, name)
);