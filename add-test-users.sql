-- Script pour ajouter 5 nouveaux utilisateurs de test
-- Mot de passe : admin123 (même que l'admin)

-- Utilisateur 1
INSERT INTO users (email, password, first_name, last_name, role, phone, address, city, country, is_active, is_enabled, is_guest)
VALUES (
    'user1@souqly.com',
    '$2a$10$0ihYbtD1tiHgHhZuC5ytRunXuraUbWn.LJiPh55xYtbYhoSR4gnAu', -- admin123
    'Jean',
    'Dupont',
    'USER',
    '+33123456789',
    '123 Rue de la Paix',
    'Paris',
    'France',
    true,
    true,
    false
);

-- Utilisateur 2
INSERT INTO users (email, password, first_name, last_name, role, phone, address, city, country, is_active, is_enabled, is_guest)
VALUES (
    'user2@souqly.com',
    '$2a$10$0ihYbtD1tiHgHhZuC5ytRunXuraUbWn.LJiPh55xYtbYhoSR4gnAu', -- admin123
    'Marie',
    'Martin',
    'USER',
    '+33123456790',
    '456 Avenue des Champs',
    'Lyon',
    'France',
    true,
    true,
    false
);

-- Utilisateur 3
INSERT INTO users (email, password, first_name, last_name, role, phone, address, city, country, is_active, is_enabled, is_guest)
VALUES (
    'user3@souqly.com',
    '$2a$10$0ihYbtD1tiHgHhZuC5ytRunXuraUbWn.LJiPh55xYtbYhoSR4gnAu', -- admin123
    'Pierre',
    'Bernard',
    'USER',
    '+33123456791',
    '789 Boulevard Central',
    'Marseille',
    'France',
    true,
    true,
    false
);

-- Utilisateur 4
INSERT INTO users (email, password, first_name, last_name, role, phone, address, city, country, is_active, is_enabled, is_guest)
VALUES (
    'user4@souqly.com',
    '$2a$10$0ihYbtD1tiHgHhZuC5ytRunXuraUbWn.LJiPh55xYtbYhoSR4gnAu', -- admin123
    'Sophie',
    'Petit',
    'USER',
    '+33123456792',
    '321 Rue du Commerce',
    'Toulouse',
    'France',
    true,
    true,
    false
);

-- Utilisateur 5
INSERT INTO users (email, password, first_name, last_name, role, phone, address, city, country, is_active, is_enabled, is_guest)
VALUES (
    'user5@souqly.com',
    '$2a$10$0ihYbtD1tiHgHhZuC5ytRunXuraUbWn.LJiPh55xYtbYhoSR4gnAu', -- admin123
    'Lucas',
    'Moreau',
    'USER',
    '+33123456793',
    '654 Place de la République',
    'Nantes',
    'France',
    true,
    true,
    false
);

-- Affichage des utilisateurs créés
SELECT 'Utilisateurs de test créés avec succès !' as message;
SELECT email, first_name, last_name, role FROM users WHERE email LIKE 'user%@souqly.com'; 