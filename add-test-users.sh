#!/bin/bash

echo "👥 Ajout des utilisateurs de test..."

# Vérifier que PostgreSQL est en cours d'exécution
if ! docker-compose ps postgres | grep -q "Up"; then
    echo "❌ PostgreSQL n'est pas en cours d'exécution"
    echo "💡 Démarrage de PostgreSQL..."
    docker-compose up -d postgres
    
    # Attendre que PostgreSQL soit prêt
    echo "⏳ Attente que PostgreSQL soit prêt..."
    sleep 10
fi

# Vérifier que PostgreSQL est prêt
until docker-compose exec -T postgres pg_isready -U postgres; do
    echo "⏳ Attente que PostgreSQL soit complètement prêt..."
    sleep 2
done

echo "📝 Exécution du script SQL..."

# Exécuter le script SQL
docker-compose exec -T postgres psql -U postgres -d souqly -f /backups/add-test-users.sql

if [ $? -eq 0 ]; then
    echo "✅ Utilisateurs de test ajoutés avec succès !"
    echo ""
    echo "📋 Liste des utilisateurs créés :"
    echo "   - user1@souqly.com (Jean Dupont)"
    echo "   - user2@souqly.com (Marie Martin)"
    echo "   - user3@souqly.com (Pierre Bernard)"
    echo "   - user4@souqly.com (Sophie Petit)"
    echo "   - user5@souqly.com (Lucas Moreau)"
    echo ""
    echo "🔑 Mot de passe pour tous : admin123"
    echo ""
    echo "🎯 Vous pouvez maintenant vous connecter avec n'importe lequel de ces comptes !"
else
    echo "❌ Erreur lors de l'ajout des utilisateurs"
    exit 1
fi 