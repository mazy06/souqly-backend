-- Migration pour ajouter les champs de profil utilisateur
-- Ajout des colonnes pour les informations personnelles complètes

-- Ajouter la colonne téléphone
ALTER TABLE users ADD COLUMN IF NOT EXISTS phone VARCHAR(20);

-- Ajouter les colonnes d'adresse
ALTER TABLE users ADD COLUMN IF NOT EXISTS address_street VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS address_city VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS address_postal_code VARCHAR(20);
ALTER TABLE users ADD COLUMN IF NOT EXISTS address_country VARCHAR(100);

-- Créer un index sur le téléphone pour les recherches rapides
CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone);

-- Créer un index sur la ville pour les recherches géographiques
CREATE INDEX IF NOT EXISTS idx_users_city ON users(address_city);

-- Commentaires pour documenter les nouvelles colonnes
COMMENT ON COLUMN users.phone IS 'Numéro de téléphone de l''utilisateur';
COMMENT ON COLUMN users.address_street IS 'Rue et numéro de l''adresse';
COMMENT ON COLUMN users.address_city IS 'Ville de l''adresse';
COMMENT ON COLUMN users.address_postal_code IS 'Code postal de l''adresse';
COMMENT ON COLUMN users.address_country IS 'Pays de l''adresse'; 