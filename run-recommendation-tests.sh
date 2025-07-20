#!/bin/bash

# Script de test de l'algorithme de recommandation Souqly
# Ce script exécute les tests et analyse les résultats

echo "🧪 Test de l'algorithme de recommandation Souqly"
echo "================================================"

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

# Vérifier si le serveur backend est en cours d'exécution
check_backend() {
    print_status "Vérification de la connexion au serveur backend..."
    
    if curl -s http://localhost:8080/api/health > /dev/null; then
        print_success "Serveur backend connecté"
        return 0
    else
        print_error "Serveur backend non accessible"
        print_status "Démarrage du serveur backend..."
        
        # Démarrer le serveur backend en arrière-plan
        cd /Users/toufik/Desktop/env/projets/Souqly/souqly-backend
        ./gradlew bootRun > /dev/null 2>&1 &
        BACKEND_PID=$!
        
        # Attendre que le serveur démarre
        sleep 10
        
        if curl -s http://localhost:8080/api/health > /dev/null; then
            print_success "Serveur backend démarré avec succès"
            return 0
        else
            print_error "Impossible de démarrer le serveur backend"
            return 1
        fi
    fi
}

# Charger les données de test
load_test_data() {
    print_status "Chargement des données de test..."
    
    # Exécuter le script SQL
    if mysql -u root -p souqly < generate-test-data.sql; then
        print_success "Données de test chargées avec succès"
        return 0
    else
        print_error "Erreur lors du chargement des données de test"
        return 1
    fi
}

# Exécuter les tests de recommandation
run_recommendation_tests() {
    print_status "Exécution des tests de recommandation..."
    
    # Installer axios si nécessaire
    if ! npm list axios > /dev/null 2>&1; then
        print_status "Installation d'axios..."
        npm install axios
    fi
    
    # Exécuter le script de test
    if node test-recommendation-algorithm.js; then
        print_success "Tests de recommandation terminés avec succès"
        return 0
    else
        print_error "Erreur lors des tests de recommandation"
        return 1
    fi
}

# Analyser les résultats
analyze_results() {
    print_status "Analyse des résultats..."
    
    echo ""
    echo "📊 ANALYSE DES RÉSULTATS"
    echo "========================="
    
    # Statistiques des recommandations
    echo ""
    echo "🎯 Statistiques des recommandations:"
    echo "   - Content-Based: $(curl -s http://localhost:8080/api/recommendations/for-me?type=content&limit=5 | jq '. | length') produits"
    echo "   - Collaborative: $(curl -s http://localhost:8080/api/recommendations/for-me?type=collaborative&limit=5 | jq '. | length') produits"
    echo "   - Hybrid: $(curl -s http://localhost:8080/api/recommendations/for-me?type=hybrid&limit=5 | jq '. | length') produits"
    
    # Analyse des boosts
    echo ""
    echo "🔥 Analyse des produits boostés:"
    BOOSTED_COUNT=$(curl -s http://localhost:8080/api/products | jq '[.[] | select(.isBoosted == true)] | length')
    TOTAL_COUNT=$(curl -s http://localhost:8080/api/products | jq '. | length')
    BOOST_PERCENTAGE=$(echo "scale=1; $BOOSTED_COUNT * 100 / $TOTAL_COUNT" | bc)
    echo "   - Produits boostés: $BOOSTED_COUNT/$TOTAL_COUNT ($BOOST_PERCENTAGE%)"
    
    # Performance des algorithmes
    echo ""
    echo "⏱️ Performance des algorithmes:"
    for algo in content collaborative hybrid; do
        START_TIME=$(date +%s%N)
        curl -s "http://localhost:8080/api/recommendations/for-me?type=$algo&limit=10" > /dev/null
        END_TIME=$(date +%s%N)
        RESPONSE_TIME=$(echo "scale=2; ($END_TIME - $START_TIME) / 1000000" | bc)
        echo "   - $algo: ${RESPONSE_TIME}ms"
    done
}

# Nettoyer les processus
cleanup() {
    if [ ! -z "$BACKEND_PID" ]; then
        print_status "Arrêt du serveur backend..."
        kill $BACKEND_PID 2>/dev/null
    fi
}

# Gestion des signaux pour le nettoyage
trap cleanup EXIT INT TERM

# Fonction principale
main() {
    echo ""
    print_status "Démarrage des tests..."
    
    # Vérifier le backend
    if ! check_backend; then
        print_error "Impossible de se connecter au backend"
        exit 1
    fi
    
    # Charger les données de test
    if ! load_test_data; then
        print_error "Impossible de charger les données de test"
        exit 1
    fi
    
    # Exécuter les tests
    if ! run_recommendation_tests; then
        print_error "Erreur lors des tests"
        exit 1
    fi
    
    # Analyser les résultats
    analyze_results
    
    echo ""
    print_success "Tests terminés avec succès !"
    echo ""
    echo "📋 Résumé:"
    echo "   ✅ Serveur backend connecté"
    echo "   ✅ Données de test chargées"
    echo "   ✅ Tests de recommandation exécutés"
    echo "   ✅ Analyse des résultats effectuée"
    echo ""
    echo "🎯 Prochaines étapes:"
    echo "   - Analyser les logs détaillés"
    echo "   - Ajuster les paramètres d'algorithme"
    echo "   - Optimiser les performances"
    echo "   - Tester avec plus de données"
}

# Exécuter le script principal
main "$@" 