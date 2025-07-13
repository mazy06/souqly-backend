-- Ajouter les colonnes de localisation à la table products
ALTER TABLE products ADD COLUMN latitude DOUBLE PRECISION;
ALTER TABLE products ADD COLUMN longitude DOUBLE PRECISION;
ALTER TABLE products ADD COLUMN location_name VARCHAR(255);

-- Ajouter des données d'exemple pour les produits existants (Paris)
UPDATE products SET 
    latitude = 48.8566, 
    longitude = 2.3522, 
    location_name = 'Paris, France' 
WHERE latitude IS NULL; 