#!/bin/bash

echo "🌱 Alimentation de la base de données Souqly avec des données de test..."

# Vérifier que Docker est en cours d'exécution
if ! docker ps | grep -q "souqly-backend-postgres"; then
    echo "❌ Le conteneur PostgreSQL n'est pas en cours d'exécution."
    echo "💡 Lancez d'abord: docker-compose up -d"
    exit 1
fi

# Attendre que PostgreSQL soit prêt
echo "⏳ Attente que PostgreSQL soit prêt..."
until docker-compose exec -T postgres pg_isready -U postgres; do
    echo "⏳ Attente que PostgreSQL soit complètement prêt..."
    sleep 2
done

# Exécuter le script SQL
echo "📝 Exécution du script de génération de données..."
docker-compose exec -T postgres psql -U postgres -d souqly < seed-database.sql

if [ $? -eq 0 ]; then
    echo "✅ Base de données alimentée avec succès !"
    echo ""
    echo "📊 Données ajoutées :"
    echo "   - 10 catégories"
    echo "   - 24 produits avec descriptions réalistes"
    echo "   - 24 images de produits"
    echo "   - 20 favoris répartis entre les utilisateurs"
    echo "   - 5 conversations entre utilisateurs"
    echo "   - 15 messages de chat"
    echo ""
    echo "🔗 Vous pouvez maintenant tester l'application avec ces données !"
else
    echo "❌ Erreur lors de l'alimentation de la base de données."
    exit 1
fi 