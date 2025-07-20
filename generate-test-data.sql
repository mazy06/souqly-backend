-- Script de génération de données de test pour l'algorithme de recommandation
-- Ce script crée des produits, utilisateurs, interactions et boosts pour tester l'algorithme

-- Nettoyer les données existantes (optionnel)
-- DELETE FROM user_interactions;
-- DELETE FROM product_boosts;
-- DELETE FROM products;
-- DELETE FROM users;

-- 1. Créer des utilisateurs de test
INSERT INTO users (id, email, password, first_name, last_name, created_at, updated_at) VALUES
(1, 'test.user1@souqly.com', '$2a$10$test', 'Jean', 'Dupont', NOW(), NOW()),
(2, 'test.user2@souqly.com', '$2a$10$test', 'Marie', 'Martin', NOW(), NOW()),
(3, 'test.user3@souqly.com', '$2a$10$test', 'Pierre', 'Bernard', NOW(), NOW()),
(4, 'test.user4@souqly.com', '$2a$10$test', 'Sophie', 'Petit', NOW(), NOW()),
(5, 'test.user5@souqly.com', '$2a$10$test', 'Lucas', 'Robert', NOW(), NOW());

-- 2. Créer des catégories de test
INSERT INTO categories (id, name, key, description, created_at, updated_at) VALUES
(1, 'Électronique', 'electronics', 'Produits électroniques', NOW(), NOW()),
(2, 'Mode', 'fashion', 'Vêtements et accessoires', NOW(), NOW()),
(3, 'Maison', 'home', 'Articles pour la maison', NOW(), NOW()),
(4, 'Sport', 'sports', 'Équipements sportifs', NOW(), NOW()),
(5, 'Loisirs', 'hobbies', 'Activités de loisirs', NOW(), NOW());

-- 3. Créer des produits de test avec différentes caractéristiques
INSERT INTO products (id, title, description, price, brand, condition, size, city, country, category_id, seller_id, favorite_count, view_count, is_active, created_at, updated_at) VALUES
-- Produits Électronique (boostés)
(1, 'iPhone 13 Pro Max - Excellent état', 'iPhone 13 Pro Max 256GB en excellent état, vendu avec tous ses accessoires.', 899.99, 'Apple', 'excellent', NULL, 'Paris', 'France', 1, 1, 15, 120, true, NOW(), NOW()),
(2, 'MacBook Pro M1 13 pouces', 'MacBook Pro M1 13 pouces 512GB, comme neuf, garantie Apple.', 1299.99, 'Apple', 'excellent', NULL, 'Paris', 'France', 1, 2, 25, 200, true, NOW(), NOW()),
(3, 'Samsung Galaxy S21', 'Samsung Galaxy S21 128GB, très bon état, boîte d\'origine.', 599.99, 'Samsung', 'good', NULL, 'Toulouse', 'France', 1, 3, 6, 35, true, NOW(), NOW()),
(4, 'Sony WH-1000XM4', 'Casque Sony WH-1000XM4, excellent état, réduction de bruit.', 299.99, 'Sony', 'excellent', NULL, 'Paris', 'France', 1, 4, 18, 95, true, NOW(), NOW()),
(5, 'iPad Air 2020', 'iPad Air 2020 64GB WiFi, parfait état, avec Apple Pencil.', 499.99, 'Apple', 'excellent', NULL, 'Lyon', 'France', 1, 5, 12, 78, true, NOW(), NOW()),

-- Produits Mode (boostés)
(6, 'Nike Air Max 270 - Taille 42', 'Chaussures Nike Air Max 270 en parfait état, portées seulement quelques fois.', 89.99, 'Nike', 'good', '42', 'Lyon', 'France', 2, 1, 8, 45, true, NOW(), NOW()),
(7, 'Adidas Ultraboost 21', 'Adidas Ultraboost 21 taille 43, comme neuves, jamais portées.', 149.99, 'Adidas', 'new', '43', 'Marseille', 'France', 2, 2, 12, 78, true, NOW(), NOW()),
(8, 'Levi\'s 501 Jeans', 'Levi\'s 501 Jeans taille 32/32, bon état, coupe classique.', 49.99, 'Levi\'s', 'good', '32/32', 'Bordeaux', 'France', 2, 3, 4, 22, true, NOW(), NOW()),
(9, 'Zara Blazer Femme', 'Blazer Zara femme taille M, neuf avec étiquette, élégant.', 79.99, 'Zara', 'new', 'M', 'Lyon', 'France', 2, 4, 3, 18, true, NOW(), NOW()),
(10, 'Converse Chuck Taylor', 'Converse Chuck Taylor taille 41, bon état, style vintage.', 39.99, 'Converse', 'good', '41', 'Nantes', 'France', 2, 5, 7, 33, true, NOW(), NOW()),

-- Produits Maison
(11, 'Machine à café Nespresso', 'Machine à café Nespresso Vertuo, excellent état, avec 50 capsules.', 129.99, 'Nespresso', 'excellent', NULL, 'Paris', 'France', 3, 1, 9, 56, true, NOW(), NOW()),
(12, 'Mixeur KitchenAid', 'Mixeur KitchenAid Artisan, rouge, parfait état, accessoires inclus.', 199.99, 'KitchenAid', 'excellent', NULL, 'Lyon', 'France', 3, 2, 5, 28, true, NOW(), NOW()),
(13, 'Lampe de bureau IKEA', 'Lampe de bureau IKEA, design moderne, état neuf.', 29.99, 'IKEA', 'new', NULL, 'Marseille', 'France', 3, 3, 2, 15, true, NOW(), NOW()),

-- Produits Sport
(14, 'Vélo de route Cannondale', 'Vélo de route Cannondale CAAD12, excellent état, équipement Shimano.', 899.99, 'Cannondale', 'excellent', '54cm', 'Toulouse', 'France', 4, 4, 11, 67, true, NOW(), NOW()),
(15, 'Raquette de tennis Wilson', 'Raquette de tennis Wilson Pro Staff, très bon état, cordage récent.', 89.99, 'Wilson', 'good', NULL, 'Bordeaux', 'France', 4, 5, 6, 34, true, NOW(), NOW()),
(16, 'Tapis de yoga Lululemon', 'Tapis de yoga Lululemon, neuf, épaisseur 5mm, antidérapant.', 49.99, 'Lululemon', 'new', NULL, 'Nantes', 'France', 4, 1, 4, 21, true, NOW(), NOW()),

-- Produits Loisirs
(17, 'Guitare acoustique Yamaha', 'Guitare acoustique Yamaha F310, excellent état, accordée.', 199.99, 'Yamaha', 'excellent', NULL, 'Paris', 'France', 5, 2, 8, 42, true, NOW(), NOW()),
(18, 'Appareil photo Canon EOS', 'Canon EOS 2000D avec objectif 18-55mm, très bon état.', 399.99, 'Canon', 'good', NULL, 'Lyon', 'France', 5, 3, 7, 38, true, NOW(), NOW()),
(19, 'Jeu de société Catan', 'Jeu de société Catan, complet, excellent état, boîte d\'origine.', 29.99, 'Catan', 'excellent', NULL, 'Marseille', 'France', 5, 4, 3, 16, true, NOW(), NOW()),
(20, 'Livre Harry Potter Collection', 'Collection complète Harry Potter, édition collector, parfait état.', 79.99, 'Gallimard', 'excellent', NULL, 'Toulouse', 'France', 5, 5, 5, 25, true, NOW(), NOW());

-- 4. Créer des interactions utilisateur pour simuler des comportements
INSERT INTO user_interactions (id, user_id, product_id, interaction_type, created_at) VALUES
-- Utilisateur 1 (préfère électronique et Apple)
(1, 1, 1, 'VIEW', NOW() - INTERVAL '2 DAY'),
(2, 1, 1, 'FAVORITE', NOW() - INTERVAL '2 DAY'),
(3, 1, 2, 'VIEW', NOW() - INTERVAL '1 DAY'),
(4, 1, 2, 'FAVORITE', NOW() - INTERVAL '1 DAY'),
(5, 1, 4, 'VIEW', NOW() - INTERVAL '12 HOUR'),
(6, 1, 5, 'VIEW', NOW() - INTERVAL '6 HOUR'),
(7, 1, 5, 'FAVORITE', NOW() - INTERVAL '6 HOUR'),

-- Utilisateur 2 (préfère mode et Nike)
(8, 2, 6, 'VIEW', NOW() - INTERVAL '3 DAY'),
(9, 2, 6, 'FAVORITE', NOW() - INTERVAL '3 DAY'),
(10, 2, 7, 'VIEW', NOW() - INTERVAL '2 DAY'),
(11, 2, 7, 'FAVORITE', NOW() - INTERVAL '2 DAY'),
(12, 2, 8, 'VIEW', NOW() - INTERVAL '1 DAY'),
(13, 2, 10, 'VIEW', NOW() - INTERVAL '12 HOUR'),

-- Utilisateur 3 (préfère sport et équipements)
(14, 3, 14, 'VIEW', NOW() - INTERVAL '4 DAY'),
(15, 3, 14, 'FAVORITE', NOW() - INTERVAL '4 DAY'),
(16, 3, 15, 'VIEW', NOW() - INTERVAL '3 DAY'),
(17, 3, 15, 'FAVORITE', NOW() - INTERVAL '3 DAY'),
(18, 3, 16, 'VIEW', NOW() - INTERVAL '2 DAY'),
(19, 3, 16, 'FAVORITE', NOW() - INTERVAL '2 DAY'),

-- Utilisateur 4 (préfère maison et cuisine)
(20, 4, 11, 'VIEW', NOW() - INTERVAL '5 DAY'),
(21, 4, 11, 'FAVORITE', NOW() - INTERVAL '5 DAY'),
(22, 4, 12, 'VIEW', NOW() - INTERVAL '4 DAY'),
(23, 4, 12, 'FAVORITE', NOW() - INTERVAL '4 DAY'),
(24, 4, 13, 'VIEW', NOW() - INTERVAL '3 DAY'),

-- Utilisateur 5 (préfère loisirs et culture)
(25, 5, 17, 'VIEW', NOW() - INTERVAL '6 DAY'),
(26, 5, 17, 'FAVORITE', NOW() - INTERVAL '6 DAY'),
(27, 5, 18, 'VIEW', NOW() - INTERVAL '5 DAY'),
(28, 5, 18, 'FAVORITE', NOW() - INTERVAL '5 DAY'),
(29, 5, 19, 'VIEW', NOW() - INTERVAL '4 DAY'),
(30, 5, 20, 'VIEW', NOW() - INTERVAL '3 DAY'),
(31, 5, 20, 'FAVORITE', NOW() - INTERVAL '3 DAY');

-- 5. Créer des boosts pour certains produits
INSERT INTO product_boosts (id, product_id, boost_level, boost_type, start_date, end_date, is_active, created_at, updated_at) VALUES
-- Boosts Premium (niveau 3)
(1, 1, 3, 'PREMIUM', NOW(), NOW() + INTERVAL '7 DAY', true, NOW(), NOW()),
(2, 2, 3, 'PREMIUM', NOW(), NOW() + INTERVAL '7 DAY', true, NOW(), NOW()),

-- Boosts Standard (niveau 2)
(3, 4, 2, 'STANDARD', NOW(), NOW() + INTERVAL '3 DAY', true, NOW(), NOW()),
(4, 7, 2, 'STANDARD', NOW(), NOW() + INTERVAL '3 DAY', true, NOW(), NOW()),

-- Boosts Urgent (niveau 1)
(5, 5, 1, 'URGENT', NOW(), NOW() + INTERVAL '1 DAY', true, NOW(), NOW()),
(6, 14, 1, 'URGENT', NOW(), NOW() + INTERVAL '1 DAY', true, NOW(), NOW());

-- 6. Créer des profils utilisateur pour les recommandations content-based
INSERT INTO user_profiles (id, user_id, preferred_categories, preferred_brands, preferred_conditions, preferred_price_range_min, preferred_price_range_max, preferred_locations, created_at, updated_at) VALUES
(1, 1, '{"electronics": 5, "fashion": 2}', '{"Apple": 5, "Sony": 3}', '{"excellent": 4, "good": 2}', 200.00, 1500.00, '["Paris", "Lyon"]', NOW(), NOW()),
(2, 2, '{"fashion": 5, "sports": 3}', '{"Nike": 5, "Adidas": 4}', '{"new": 3, "good": 4}', 50.00, 300.00, '["Lyon", "Marseille"]', NOW(), NOW()),
(3, 3, '{"sports": 5, "hobbies": 2}', '{"Cannondale": 5, "Wilson": 4}', '{"excellent": 3, "good": 4}', 100.00, 1000.00, '["Toulouse", "Bordeaux"]', NOW(), NOW()),
(4, 4, '{"home": 5, "electronics": 2}', '{"KitchenAid": 5, "Nespresso": 4}', '{"excellent": 4, "new": 3}', 30.00, 500.00, '["Paris", "Lyon"]', NOW(), NOW()),
(5, 5, '{"hobbies": 5, "sports": 2}', '{"Yamaha": 5, "Canon": 4}', '{"excellent": 4, "good": 3}', 30.00, 500.00, '["Paris", "Lyon"]', NOW(), NOW());

-- 7. Créer des similarités utilisateur pour les recommandations collaboratives
INSERT INTO user_similarities (id, user_id1, user_id2, similarity_score, created_at, updated_at) VALUES
(1, 1, 4, 0.75, NOW(), NOW()), -- Utilisateurs 1 et 4 similaires (électronique/maison)
(2, 2, 3, 0.65, NOW(), NOW()), -- Utilisateurs 2 et 3 similaires (mode/sport)
(3, 3, 5, 0.55, NOW(), NOW()), -- Utilisateurs 3 et 5 similaires (sport/loisirs)
(4, 4, 1, 0.75, NOW(), NOW()), -- Réciproque de 1-4
(5, 5, 3, 0.55, NOW(), NOW()); -- Réciproque de 3-5

-- Afficher les statistiques des données créées
SELECT 'Statistiques des données de test' as info;
SELECT COUNT(*) as total_users FROM users WHERE email LIKE 'test.user%';
SELECT COUNT(*) as total_products FROM products;
SELECT COUNT(*) as total_interactions FROM user_interactions;
SELECT COUNT(*) as total_boosts FROM product_boosts WHERE is_active = true;
SELECT COUNT(*) as total_profiles FROM user_profiles;
SELECT COUNT(*) as total_similarities FROM user_similarities;

-- Afficher les produits boostés
SELECT 'Produits boostés' as info;
SELECT p.id, p.title, p.brand, pb.boost_level, pb.boost_type, p.favorite_count, p.view_count
FROM products p
JOIN product_boosts pb ON p.id = pb.product_id
WHERE pb.is_active = true
ORDER BY pb.boost_level DESC;

-- Afficher les interactions par utilisateur
SELECT 'Interactions par utilisateur' as info;
SELECT u.email, COUNT(ui.id) as interaction_count,
       COUNT(CASE WHEN ui.interaction_type = 'FAVORITE' THEN 1 END) as favorites,
       COUNT(CASE WHEN ui.interaction_type = 'VIEW' THEN 1 END) as views
FROM users u
LEFT JOIN user_interactions ui ON u.id = ui.user_id
WHERE u.email LIKE 'test.user%'
GROUP BY u.id, u.email
ORDER BY interaction_count DESC; 