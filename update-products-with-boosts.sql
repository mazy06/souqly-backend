-- Script pour mettre à jour les produits avec des boosts
-- Ce script ajoute des boosts à quelques produits existants

-- Mettre à jour les colonnes de boost si elles n'existent pas
DO $$ 
BEGIN
    -- Ajouter la colonne is_boosted si elle n'existe pas
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'products' AND column_name = 'is_boosted') THEN
        ALTER TABLE products ADD COLUMN is_boosted BOOLEAN DEFAULT FALSE;
    END IF;
    
    -- Ajouter la colonne boost_level si elle n'existe pas
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'products' AND column_name = 'boost_level') THEN
        ALTER TABLE products ADD COLUMN boost_level INTEGER DEFAULT 0;
    END IF;
END $$;

-- Mettre à jour quelques produits avec des boosts
UPDATE products 
SET is_boosted = true, boost_level = 3
WHERE id = 1; -- iPhone 8 (produit populaire)

UPDATE products 
SET is_boosted = true, boost_level = 2
WHERE id = 3; -- AUDI Q5 (produit premium)

UPDATE products 
SET is_boosted = true, boost_level = 1
WHERE id = 2; -- Poster (produit culturel)

-- Ajouter des boosts à d'autres produits basés sur les favoris
UPDATE products 
SET is_boosted = true, boost_level = 2
WHERE favorite_count > 0 AND id NOT IN (1, 2, 3);

-- Afficher les résultats
SELECT 
    id,
    title,
    price,
    favorite_count,
    is_boosted,
    boost_level,
    CASE 
        WHEN is_boosted THEN '🔥 Boosté'
        ELSE '📦 Standard'
    END as status
FROM products 
ORDER BY boost_level DESC, favorite_count DESC;

-- Statistiques des boosts
SELECT 
    COUNT(*) as total_products,
    COUNT(CASE WHEN is_boosted THEN 1 END) as boosted_products,
    COUNT(CASE WHEN boost_level > 0 THEN 1 END) as products_with_boost_level,
    AVG(CASE WHEN is_boosted THEN boost_level ELSE 0 END) as avg_boost_level,
    AVG(favorite_count) as avg_favorites
FROM products; 