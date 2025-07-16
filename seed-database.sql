-- Script de génération de données de test pour Souqly
-- Ce script alimente la base de données avec des données réalistes

-- 1. INSERTION DES CATÉGORIES
INSERT INTO categories (label, category_key, display_order) VALUES
('Immobilier', 'immobilier', 1),
('Emploi', 'emploi', 2),
('Services', 'services', 3),
('Mobilier', 'mobilier', 4),
('Électroménager', 'electromenager', 5),
('Vêtements', 'vetements', 6),
('Électronique', 'electronique', 7),
('Sport & Loisirs', 'sport-loisirs', 8),
('Livres & Médias', 'livres-medias', 9),
('Jardin & Bricolage', 'jardin-bricolage', 10);

-- 2. INSERTION DES PRODUITS RÉALISTES

-- Immobilier
INSERT INTO products (title, description, price, category_id, seller_id, brand, size, condition, price_with_fees, shipping_info, status, view_count, favorite_count, city, country, created_at, updated_at) VALUES
('Appartement T3 moderne', 'Bel appartement de 75m² avec 2 chambres, cuisine équipée, balcon et parking. Idéal pour jeune couple ou petite famille.', 250000.00, 1, 1, 'Particulier', '75m²', 'Comme neuf', 255000.00, 'Frais de notaire inclus', 'ACTIVE', 45, 12, 'Paris', 'France', NOW(), NOW()),
('Studio meublé centre-ville', 'Studio de 25m² entièrement meublé, proche transports et commerces. Idéal étudiant ou jeune actif.', 180000.00, 1, 2, 'Particulier', '25m²', 'Très bon état', 185000.00, 'Frais d''agence inclus', 'ACTIVE', 32, 8, 'Lyon', 'France', NOW(), NOW()),
('Maison avec jardin', 'Maison de 120m² avec 4 chambres, grand jardin, garage. Quartier calme et familial.', 380000.00, 1, 3, 'Particulier', '120m²', 'Comme neuf', 390000.00, 'Frais de notaire inclus', 'ACTIVE', 28, 15, 'Marseille', 'France', NOW(), NOW());

-- Emploi
INSERT INTO products (title, description, price, category_id, seller_id, brand, size, condition, price_with_fees, shipping_info, status, view_count, favorite_count, city, country, created_at, updated_at) VALUES
('Développeur Full Stack', 'Poste de développeur Full Stack (React/Java) pour startup innovante. CDI, télétravail possible, salaire attractif.', 45000.00, 2, 4, 'TechStart', 'CDI', 'Nouveau', 45000.00, 'Avantages inclus', 'ACTIVE', 67, 23, 'Paris', 'France', NOW(), NOW()),
('Chef de projet digital', 'Chef de projet digital pour agence web. Gestion d''équipe, projets clients, évolution possible.', 55000.00, 2, 5, 'DigitalAgency', 'CDI', 'Nouveau', 55000.00, 'Avantages inclus', 'ACTIVE', 45, 18, 'Lyon', 'France', NOW(), NOW()),
('Commercial B2B', 'Commercial B2B secteur industrie. Secteur en croissance, commission attractive, voiture de fonction.', 35000.00, 2, 1, 'IndustriePlus', 'CDI', 'Nouveau', 35000.00, 'Avantages inclus', 'ACTIVE', 38, 12, 'Marseille', 'France', NOW(), NOW());

-- Services
INSERT INTO products (title, description, price, category_id, seller_id, brand, size, condition, price_with_fees, shipping_info, status, view_count, favorite_count, city, country, created_at, updated_at) VALUES
('Cours de piano à domicile', 'Professeur de piano diplômé donne cours à domicile. Tous niveaux, tous âges. Déplacement inclus.', 50.00, 3, 2, 'MusiquePlus', '1h', 'Professionnel', 50.00, 'Déplacement inclus', 'ACTIVE', 23, 7, 'Paris', 'France', NOW(), NOW()),
('Ménage régulier', 'Service de ménage régulier, femme de ménage expérimentée. Ponctuelle et sérieuse.', 25.00, 3, 3, 'MénagePro', '2h', 'Professionnel', 25.00, 'Matériel fourni', 'ACTIVE', 34, 14, 'Lyon', 'France', NOW(), NOW()),
('Déménagement complet', 'Service de déménagement complet avec camion et équipe. Emballage, transport, déballage.', 800.00, 3, 4, 'DéménageExpress', 'Complet', 'Professionnel', 800.00, 'Assurance incluse', 'ACTIVE', 19, 6, 'Marseille', 'France', NOW(), NOW());

-- Mobilier
INSERT INTO products (title, description, price, category_id, seller_id, brand, size, condition, price_with_fees, shipping_info, status, view_count, favorite_count, city, country, created_at, updated_at) VALUES
('Canapé cuir 3 places', 'Canapé cuir véritable, 3 places, couleur marron. Très confortable, parfait état.', 800.00, 4, 5, 'MaisonModerne', '3 places', 'Très bon état', 850.00, 'Livraison possible', 'ACTIVE', 56, 21, 'Paris', 'France', NOW(), NOW()),
('Table de salle à manger', 'Table de salle à manger en bois massif, 6 personnes. Style moderne, excellent état.', 400.00, 4, 1, 'Particulier', '6 personnes', 'Très bon état', 420.00, 'À récupérer', 'ACTIVE', 42, 16, 'Lyon', 'France', NOW(), NOW()),
('Lit mezzanine enfant', 'Lit mezzanine avec bureau intégré, parfait pour chambre d''enfant. Matelas inclus.', 300.00, 4, 2, 'DécorationPlus', '90x200', 'Bon état', 320.00, 'Montage inclus', 'ACTIVE', 38, 12, 'Marseille', 'France', NOW(), NOW());

-- Électroménager
INSERT INTO products (title, description, price, category_id, seller_id, brand, size, condition, price_with_fees, shipping_info, status, view_count, favorite_count, city, country, created_at, updated_at) VALUES
('Réfrigérateur Samsung', 'Réfrigérateur Samsung 350L, classe A++. Fonctionne parfaitement, propre.', 250.00, 5, 3, 'Samsung', '350L', 'Très bon état', 270.00, 'Livraison possible', 'ACTIVE', 67, 28, 'Paris', 'France', NOW(), NOW()),
('Machine à laver Bosch', 'Machine à laver Bosch 8kg, classe A. Très fiable, économie d''énergie.', 180.00, 5, 4, 'Bosch', '8kg', 'Bon état', 200.00, 'À récupérer', 'ACTIVE', 45, 19, 'Lyon', 'France', NOW(), NOW()),
('Four électrique Whirlpool', 'Four électrique Whirlpool, 60cm, multifonctions. Parfait état, garantie restante.', 120.00, 5, 5, 'Whirlpool', '60cm', 'Très bon état', 130.00, 'Livraison possible', 'ACTIVE', 34, 15, 'Marseille', 'France', NOW(), NOW());

-- Vêtements
INSERT INTO products (title, description, price, category_id, seller_id, brand, size, condition, price_with_fees, shipping_info, status, view_count, favorite_count, city, country, created_at, updated_at) VALUES
('Blazer Zara femme', 'Blazer Zara noir, taille M. Très élégant, parfait pour le bureau.', 35.00, 6, 1, 'Zara', 'M', 'Très bon état', 38.00, 'Envoi possible', 'ACTIVE', 89, 34, 'Paris', 'France', NOW(), NOW()),
('Sneakers Nike Air Max', 'Sneakers Nike Air Max 90, taille 42. Couleur grise, très confortables.', 80.00, 6, 2, 'Nike', '42', 'Bon état', 85.00, 'Envoi possible', 'ACTIVE', 76, 29, 'Lyon', 'France', NOW(), NOW()),
('Robe de soirée', 'Robe de soirée élégante, taille S. Couleur bleu marine, parfaite pour événements.', 120.00, 6, 3, 'Particulier', 'S', 'Très bon état', 125.00, 'Envoi possible', 'ACTIVE', 54, 22, 'Marseille', 'France', NOW(), NOW());

-- Électronique
INSERT INTO products (title, description, price, category_id, seller_id, brand, size, condition, price_with_fees, shipping_info, status, view_count, favorite_count, city, country, created_at, updated_at) VALUES
('iPhone 12 128GB', 'iPhone 12 128GB, couleur bleu. Excellent état, boîte et chargeur inclus.', 450.00, 7, 4, 'Apple', '128GB', 'Très bon état', 470.00, 'Envoi possible', 'ACTIVE', 123, 45, 'Paris', 'France', NOW(), NOW()),
('MacBook Air M1', 'MacBook Air M1 256GB, excellent état. Idéal pour études ou travail.', 800.00, 7, 5, 'Apple', '256GB', 'Très bon état', 830.00, 'Livraison possible', 'ACTIVE', 98, 38, 'Lyon', 'France', NOW(), NOW()),
('PS5 avec 2 manettes', 'PlayStation 5 avec 2 manettes et 3 jeux. Parfait état, garantie restante.', 400.00, 7, 1, 'Sony', '1TB', 'Très bon état', 420.00, 'À récupérer', 'ACTIVE', 156, 67, 'Marseille', 'France', NOW(), NOW());

-- Sport & Loisirs
INSERT INTO products (title, description, price, category_id, seller_id, brand, size, condition, price_with_fees, shipping_info, status, view_count, favorite_count, city, country, created_at, updated_at) VALUES
('Vélo VTT Trek', 'Vélo VTT Trek Marlin 7, taille L. Excellent état, entretien récent.', 350.00, 8, 2, 'Trek', 'L', 'Très bon état', 370.00, 'À récupérer', 'ACTIVE', 67, 25, 'Paris', 'France', NOW(), NOW()),
('Raquettes de tennis', 'Raquettes de tennis Wilson, avec housse. Parfait état, idéal débutant.', 80.00, 8, 3, 'Wilson', 'Standard', 'Très bon état', 85.00, 'Envoi possible', 'ACTIVE', 45, 18, 'Lyon', 'France', NOW(), NOW()),
('Tapis de yoga', 'Tapis de yoga premium, antidérapant. Utilisé quelques fois seulement.', 25.00, 8, 4, 'YogaPlus', 'Standard', 'Très bon état', 28.00, 'Envoi possible', 'ACTIVE', 34, 12, 'Marseille', 'France', NOW(), NOW());

-- 3. INSERTION DES IMAGES DE PRODUITS (simulation avec des URLs)
INSERT INTO product_images (product_id, file_name, content_type) VALUES
-- Images pour Immobilier
(1, 'appartement_t3_1.jpg', 'image/jpeg'),
(1, 'appartement_t3_2.jpg', 'image/jpeg'),
(2, 'studio_centre_ville_1.jpg', 'image/jpeg'),
(3, 'maison_jardin_1.jpg', 'image/jpeg'),
(3, 'maison_jardin_2.jpg', 'image/jpeg'),

-- Images pour Emploi
(4, 'developpeur_fullstack.jpg', 'image/jpeg'),
(5, 'chef_projet_digital.jpg', 'image/jpeg'),
(6, 'commercial_b2b.jpg', 'image/jpeg'),

-- Images pour Services
(7, 'cours_piano.jpg', 'image/jpeg'),
(8, 'menage_regulier.jpg', 'image/jpeg'),
(9, 'demenagement.jpg', 'image/jpeg'),

-- Images pour Mobilier
(10, 'canape_cuir_1.jpg', 'image/jpeg'),
(11, 'table_salle_manger.jpg', 'image/jpeg'),
(12, 'lit_mezzanine.jpg', 'image/jpeg'),

-- Images pour Électroménager
(13, 'refrigerateur_samsung.jpg', 'image/jpeg'),
(14, 'machine_laver_bosch.jpg', 'image/jpeg'),
(15, 'four_whirlpool.jpg', 'image/jpeg'),

-- Images pour Vêtements
(16, 'blazer_zara.jpg', 'image/jpeg'),
(17, 'sneakers_nike.jpg', 'image/jpeg'),
(18, 'robe_soiree.jpg', 'image/jpeg'),

-- Images pour Électronique
(19, 'iphone_12.jpg', 'image/jpeg'),
(20, 'macbook_air_m1.jpg', 'image/jpeg'),
(21, 'ps5_manettes.jpg', 'image/jpeg'),

-- Images pour Sport
(22, 'velo_vtt_trek.jpg', 'image/jpeg'),
(23, 'raquettes_tennis.jpg', 'image/jpeg'),
(24, 'tapis_yoga.jpg', 'image/jpeg');

-- 4. INSERTION DES FAVORIS
INSERT INTO favorites (user_id, product_id, created_at) VALUES
-- User 1 favoris
(1, 1, NOW()), -- Appartement
(1, 4, NOW()), -- Développeur Full Stack
(1, 10, NOW()), -- Canapé cuir
(1, 19, NOW()), -- iPhone 12

-- User 2 favoris
(2, 2, NOW()), -- Studio
(2, 7, NOW()), -- Cours de piano
(2, 16, NOW()), -- Blazer Zara
(2, 22, NOW()), -- Vélo VTT

-- User 3 favoris
(3, 3, NOW()), -- Maison avec jardin
(3, 8, NOW()), -- Ménage régulier
(3, 13, NOW()), -- Réfrigérateur Samsung
(3, 20, NOW()), -- MacBook Air

-- User 4 favoris
(4, 5, NOW()), -- Chef de projet
(4, 11, NOW()), -- Table de salle à manger
(4, 17, NOW()), -- Sneakers Nike
(4, 23, NOW()), -- Raquettes de tennis

-- User 5 favoris
(5, 6, NOW()), -- Commercial B2B
(5, 12, NOW()), -- Lit mezzanine
(5, 18, NOW()), -- Robe de soirée
(5, 21, NOW()); -- PS5

-- 5. INSERTION DES CONVERSATIONS
INSERT INTO conversations (conversation_id, buyer_id, seller_id, product_id, last_message, last_message_time, unread_count_buyer, unread_count_seller, created_at, updated_at) VALUES
-- Conversation 1: User 2 achète l'appartement de User 1
('conv_1_2_1', 2, 1, 1, 'Bonjour, je suis intéressé par votre appartement. Est-il toujours disponible ?', NOW(), 0, 1, NOW(), NOW()),

-- Conversation 2: User 3 achète le studio de User 2
('conv_3_2_2', 3, 2, 2, 'Bonjour, pouvez-vous me donner plus d''informations sur le studio ?', NOW(), 0, 1, NOW(), NOW()),

-- Conversation 3: User 4 achète les cours de piano de User 2
('conv_4_2_7', 4, 2, 7, 'Bonjour, je suis intéressé par les cours de piano pour ma fille de 8 ans.', NOW(), 0, 1, NOW(), NOW()),

-- Conversation 4: User 5 achète le canapé de User 5
('conv_5_5_10', 5, 5, 10, 'Bonjour, le canapé est-il toujours disponible ?', NOW(), 0, 1, NOW(), NOW()),

-- Conversation 5: User 1 achète l'iPhone de User 4
('conv_1_4_19', 1, 4, 19, 'Bonjour, l''iPhone est-il toujours disponible ?', NOW(), 0, 1, NOW(), NOW());

-- 6. INSERTION DES MESSAGES DE CHAT
INSERT INTO chat_messages (conversation_id, sender_id, message, message_type, created_at) VALUES
-- Messages pour conversation 1
('conv_1_2_1', 2, 'Bonjour, je suis intéressé par votre appartement. Est-il toujours disponible ?', 'TEXT', NOW()),
('conv_1_2_1', 1, 'Bonjour ! Oui, l''appartement est toujours disponible. Souhaitez-vous une visite ?', 'TEXT', NOW()),
('conv_1_2_1', 2, 'Oui, ce serait parfait. Quand seriez-vous disponible ?', 'TEXT', NOW()),

-- Messages pour conversation 2
('conv_3_2_2', 3, 'Bonjour, pouvez-vous me donner plus d''informations sur le studio ?', 'TEXT', NOW()),
('conv_3_2_2', 2, 'Bonjour ! Le studio fait 25m², il est entièrement meublé et proche du métro.', 'TEXT', NOW()),
('conv_3_2_2', 3, 'Parfait ! Pouvez-vous m''envoyer plus de photos ?', 'TEXT', NOW()),

-- Messages pour conversation 3
('conv_4_2_7', 4, 'Bonjour, je suis intéressé par les cours de piano pour ma fille de 8 ans.', 'TEXT', NOW()),
('conv_4_2_7', 2, 'Bonjour ! J''ai de l''expérience avec les enfants. Quand souhaitez-vous commencer ?', 'TEXT', NOW()),
('conv_4_2_7', 4, 'Le mercredi après-midi serait parfait.', 'TEXT', NOW()),

-- Messages pour conversation 4
('conv_5_5_10', 5, 'Bonjour, le canapé est-il toujours disponible ?', 'TEXT', NOW()),
('conv_5_5_10', 5, 'Oui, il est toujours disponible. Souhaitez-vous le voir ?', 'TEXT', NOW()),
('conv_5_5_10', 5, 'Oui, quand puis-je passer ?', 'TEXT', NOW()),

-- Messages pour conversation 5
('conv_1_4_19', 1, 'Bonjour, l''iPhone est-il toujours disponible ?', 'TEXT', NOW()),
('conv_1_4_19', 4, 'Oui, il est toujours disponible. Il est en parfait état.', 'TEXT', NOW()),
('conv_1_4_19', 1, 'Parfait ! Pouvez-vous me l''envoyer ?', 'TEXT', NOW());

-- 7. MISE À JOUR DES COMPTEURS DE FAVORIS
UPDATE products SET favorite_count = (
    SELECT COUNT(*) FROM favorites WHERE product_id = products.id
);

-- 8. AFFICHAGE DES STATISTIQUES
SELECT 'Base de données alimentée avec succès !' as message;
SELECT 'Statistiques:' as info;
SELECT COUNT(*) as total_products FROM products;
SELECT COUNT(*) as total_favorites FROM favorites;
SELECT COUNT(*) as total_conversations FROM conversations;
SELECT COUNT(*) as total_messages FROM chat_messages; 