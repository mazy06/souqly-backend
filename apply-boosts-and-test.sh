#!/bin/bash

echo "🚀 Application des boosts et test des recommandations"
echo "=================================================="

# Configuration
DB_CONTAINER="souqly-backend-postgres-1"
DB_NAME="souqly"
DB_USER="postgres"

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Fonction pour afficher les messages
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Étape 1: Appliquer les boosts
print_status "Étape 1: Application des boosts aux produits..."

# Vérifier si Docker est disponible
if ! command -v docker &> /dev/null; then
    print_error "Docker n'est pas installé ou n'est pas dans le PATH"
    exit 1
fi

# Vérifier si le container PostgreSQL existe
if ! docker ps | grep -q $DB_CONTAINER; then
    print_error "Container PostgreSQL $DB_CONTAINER non trouvé"
    exit 1
fi

# Appliquer les boosts via Docker
print_status "Exécution du script SQL de mise à jour..."
docker exec -i $DB_CONTAINER psql -U $DB_USER -d $DB_NAME < update-products-with-boosts.sql

if [ $? -eq 0 ]; then
    print_success "Boosts appliqués avec succès"
else
    print_error "Erreur lors de l'application des boosts"
    exit 1
fi

echo ""

# Étape 2: Vérifier les produits boostés
print_status "Étape 2: Vérification des produits boostés..."

# Récupérer les produits boostés
BOOSTED_PRODUCTS=$(docker exec $DB_CONTAINER psql -U $DB_USER -d $DB_NAME -t -c "
SELECT 
    id,
    title,
    price,
    favorite_count,
    boost_level
FROM products 
WHERE is_boosted = true 
ORDER BY boost_level DESC, favorite_count DESC;
")

if [ $? -eq 0 ]; then
    print_success "Produits boostés récupérés"
    echo "$BOOSTED_PRODUCTS" | while IFS='|' read -r id title price favorites boost_level; do
        if [ ! -z "$id" ]; then
            echo "   🔥 ID: $id | $title | Prix: ${price}€ | Favoris: $favorites | Boost: $boost_level"
        fi
    done
else
    print_error "Erreur lors de la récupération des produits boostés"
fi

echo ""

# Étape 3: Tester les recommandations
print_status "Étape 3: Test des recommandations..."

# Attendre un peu pour que les changements soient pris en compte
sleep 2

# Tester l'endpoint de produits
print_status "Test de l'endpoint /products..."
PRODUCTS_RESPONSE=$(curl -s "http://localhost:8080/api/products")
if [ $? -eq 0 ]; then
    BOOSTED_COUNT=$(echo "$PRODUCTS_RESPONSE" | jq '.content[] | select(.isBoosted == true) | .id' | wc -l)
    print_success "Endpoint /products accessible ($BOOSTED_COUNT produits boostés détectés)"
else
    print_error "Erreur lors du test de l'endpoint /products"
fi

# Tester l'endpoint de recommandations (même s'il retourne 403)
print_status "Test de l'endpoint /recommendations/for-me..."
RECOMMENDATIONS_RESPONSE=$(curl -s -w "%{http_code}" "http://localhost:8080/api/recommendations/for-me?limit=5")
HTTP_CODE="${RECOMMENDATIONS_RESPONSE: -3}"
RESPONSE_BODY="${RECOMMENDATIONS_RESPONSE%???}"

if [ "$HTTP_CODE" = "200" ]; then
    print_success "Endpoint /recommendations/for-me accessible"
    RECOMMENDATIONS_COUNT=$(echo "$RESPONSE_BODY" | jq '.recommendations | length')
    echo "   📦 $RECOMMENDATIONS_COUNT recommandations trouvées"
else
    print_warning "Endpoint /recommendations/for-me retourne $HTTP_CODE (probablement un problème d'authentification)"
fi

echo ""

# Étape 4: Exécuter le script de test Node.js
print_status "Étape 4: Exécution du script de test Node.js..."

if [ -f "test-production-recommendations.js" ]; then
    node test-production-recommendations.js
    if [ $? -eq 0 ]; then
        print_success "Script de test exécuté avec succès"
    else
        print_error "Erreur lors de l'exécution du script de test"
    fi
else
    print_error "Script test-production-recommendations.js non trouvé"
fi

echo ""

# Étape 5: Résumé
print_status "Étape 5: Résumé des actions..."

# Statistiques finales
FINAL_STATS=$(docker exec $DB_CONTAINER psql -U $DB_USER -d $DB_NAME -t -c "
SELECT 
    COUNT(*) as total,
    COUNT(CASE WHEN is_boosted THEN 1 END) as boosted,
    AVG(CASE WHEN is_boosted THEN boost_level ELSE 0 END) as avg_boost,
    AVG(favorite_count) as avg_favorites
FROM products;
")

print_success "Statistiques finales:"
echo "$FINAL_STATS" | while IFS='|' read -r total boosted avg_boost avg_favorites; do
    if [ ! -z "$total" ]; then
        echo "   📊 Total produits: $total"
        echo "   🔥 Produits boostés: $boosted"
        echo "   📈 Niveau de boost moyen: $avg_boost"
        echo "   ⭐ Favoris moyens: $avg_favorites"
    fi
done

echo ""
print_success "🎉 Processus terminé !"
echo ""
echo "📋 Prochaines étapes:"
echo "   1. Résoudre le problème d'authentification (403) pour les recommandations"
echo "   2. Intégrer les recommandations dans le frontend"
echo "   3. Tester avec des utilisateurs réels"
echo "   4. Optimiser les performances"
echo "" 