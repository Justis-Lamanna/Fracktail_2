CREATE TABLE IF NOT EXISTS permissions (
    id INT PRIMARY KEY AUTOINCREMENT,
    user_snowflake CHAR(24) NOT NULL,
    guild_snowflake CHAR(24),
    permission CHAR(50) NOT NULL
);