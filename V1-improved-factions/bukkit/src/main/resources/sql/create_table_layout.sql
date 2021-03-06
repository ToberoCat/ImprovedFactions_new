-- Test variables
-- SET @max_len := 10;
-- SET @max_tag := 3;

CREATE SCHEMA IF NOT EXISTS improved_factions;
USE improved_factions;

-- Create faction table
CREATE TABLE IF NOT EXISTS factions
(
    registry_id    varchar(@max_len)             not null, -- Registry max size
    display_name   varchar(@max_len)             not null, -- Display max size
    motd           text    default 'New faction' not null,
    tag            varchar(@max_tag)             not null, -- Tag max size
    open_type      int(3)                        not null,
    frozen         boolean default false         not null,
    permanent      boolean default false         not null,
    created_at     datetime                      not null,
    owner          char(36)                      not null,
    claimed_chunks int     default 0             not null,
    balance        bigint  default 0             not null,
    current_power  int                           not null,
    max_power      int                           not null,
    constraint factions_pk
        primary key (registry_id)
);

-- Create faction description table
create table faction_descriptions
(
    registry_id VARCHAR(@max_len) not null,
    line        int               not null,
    content     TEXT              not null,
    PRIMARY KEY (registry_id, line)
);

-- Create faction bans
create table IF NOT EXISTS faction_bans
(
    registry_id VARCHAR(@max_len) not null,
    banned      CHAR(36)          not null,
    constraint faction_bans_pk
        primary key (banned)
);

-- Faction relation table
create table IF NOT EXISTS faction_relations
(
    registry_id          VARCHAR(@max_len) not null,
    relation_registry_id VARCHAR(@max_len) not null,
    relation_status      INT(3) default 1  not null, -- Only options: 0 - ally, 1 neutral, 2 enemy
    constraint faction_relations_pk
        primary key (relation_registry_id)
);

-- Create settings table
create table IF NOT EXISTS faction_settings
(
    registry_id VARCHAR(@max_len) not null,
    setting     TEXT              not null,
    type        TEXT              not null,
    value       TEXT              null,
    constraint faction_settings_pk
        primary key (registry_id)
);

-- Create players
CREATE TABLE IF NOT EXISTS players
(
    uuid        CHAR(36)          NOT NULL,
    faction     VARCHAR(@max_len) NOT NULL,
    member_rank TEXT              NOT NULL,
    power       DOUBLE            NOT NULL,
    maxPower    DOUBLE            NOT NULL,
    timeout     DATETIME,
    CONSTRAINT players_pk
        PRIMARY KEY (uuid)
);

-- Create player settings
create table IF NOT EXISTS player_settings
(
    uuid    CHAR(36) not null,
    setting TEXT     not null,
    value   TEXT     not null,
    constraint player_settings_pk
        primary key (uuid)
);

-- Create messages
create table messages
(
    player  char(36)     not null,
    content varchar(255) not null,
    constraint messages_pk
        PRIMARY KEY (player, content)
);



