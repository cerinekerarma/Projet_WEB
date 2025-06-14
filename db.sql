CREATE TABLE "User" (
                        login VARCHAR(255) PRIMARY KEY,
                        email VARCHAR(255),
                        password BYTEA,
                        date_creation DATE
);

CREATE TABLE "Server" (
                          id_server INTEGER PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
                          nom VARCHAR(255),
                          id_admin VARCHAR(255) NOT NULL,
                          FOREIGN KEY (id_admin) REFERENCES "User"(login)
);

CREATE TABLE "Message" (
                           id_message INTEGER PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
                           contenu VARCHAR(255),
                           date_envoie DATE
);

-- Un utilisateur publie un message dans un serveur
CREATE TABLE "publier" (
                           id_message INTEGER,
                           id_server INTEGER,
                           id_user VARCHAR(255),
                           PRIMARY KEY(id_message),
                           FOREIGN KEY (id_message) REFERENCES "Message"(id_message),
                           FOREIGN KEY (id_server) REFERENCES "Server"(id_server),
                           FOREIGN KEY (id_user) REFERENCES "User"(login)
);

-- Un utilisateur écrit un message à un autre utilisateur (discussion privée)
CREATE TABLE "ecrire" (
                          id_message INTEGER,
                          id_user1 VARCHAR(255), -- émetteur
                          id_user2 VARCHAR(255), -- destinataire
                          PRIMARY KEY(id_message),
                          FOREIGN KEY (id_message) REFERENCES "Message"(id_message),
                          FOREIGN KEY (id_user1) REFERENCES "User"(login),
                          FOREIGN KEY (id_user2) REFERENCES "User"(login)
);

-- Un utilisateur rejoint un serveur
CREATE TABLE "integrer" (
                            id_user VARCHAR(255),
                            id_server INTEGER,
                            PRIMARY KEY(id_user, id_server),
                            FOREIGN KEY (id_user) REFERENCES "User"(login),
                            FOREIGN KEY (id_server) REFERENCES "Server"(id_server)
);

-- Un utilisateur émet une réaction à un message
CREATE TABLE "emettre" (
                           id_message INTEGER,
                           id_user VARCHAR(255),
                           reaction VARCHAR(255),
                           PRIMARY KEY(id_message, id_user),
                           FOREIGN KEY (id_message) REFERENCES "Message"(id_message),
                           FOREIGN KEY (id_user) REFERENCES "User"(login)
);

-- CREATE USER discord_user WITH PASSWORD 'discord_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO discord_user;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

       -- Utilisateurs
INSERT INTO "User" (login, email, password, date_creation) VALUES
                                                               ('alice','alice@example.com', digest('passwordalice', 'sha256'), CURRENT_DATE),
                                                               ('bob','bob@example.com', digest('passwordbob', 'sha256'), CURRENT_DATE),
                                                               ('charlie','charlie@example.com', digest('passwordcharlie', 'sha256'), CURRENT_DATE);

-- Serveurs
INSERT INTO "Server" (nom, id_admin) VALUES
                                         ('Serveur Général', 'alice'),
                                         ('Serveur Projet', 'bob');

-- Intégrations utilisateurs dans serveurs
INSERT INTO "integrer" (id_user, id_server) VALUES
                                                ('alice', 1),
                                                ('bob', 1),
                                                ('charlie', 2);

-- Messages
INSERT INTO "Message" (contenu, date_envoie) VALUES
                                                 ('Bonjour tout le monde !', CURRENT_DATE),
                                                 ('Comment avance le projet ?', CURRENT_DATE),
                                                 ('On se retrouve à 15h ?', CURRENT_DATE);

-- Publier messages dans serveurs (message_id, server_id, user_id)
INSERT INTO "publier" (id_message, id_server, id_user) VALUES
                                                           (1, 1, 'alice'),
                                                           (2, 1, 'bob'),
                                                           (3, 2, 'charlie');

-- Messages privés (discussion directe)
INSERT INTO "ecrire" (id_message, id_user1, id_user2) VALUES
                                                          (2, 'bob', 'alice'),
                                                          (3, 'charlie', 'bob');
-- Réactions
INSERT INTO "emettre" (id_message, id_user, reaction) VALUES
                                                          (1, 'bob', 'like'),
                                                          (1, 'charlie', 'love'),
                                                          (2, 'alice', 'haha');

ALTER TABLE "Message" ALTER COLUMN contenu TYPE TEXT;
