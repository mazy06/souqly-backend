#!/bin/bash

echo "🔄 Restauration de la base de données PostgreSQL..."

# Trouver le fichier de backup le plus récent
LATEST_BACKUP=$(ls -t ./backups/souqly_backup_*.sql 2>/dev/null | head -1)

if [ -z "$LATEST_BACKUP" ]; then
    echo "❌ Aucun fichier de backup trouvé dans ./backups/"
    echo "💡 Assurez-vous d'avoir créé un backup avec ./backup-db.sh"
    exit 1
fi

echo "📦 Fichier de backup trouvé : $LATEST_BACKUP"
echo "📊 Taille du fichier : $(du -h "$LATEST_BACKUP" | cut -f1)"

# Démarrer PostgreSQL
echo "🚀 Démarrage de PostgreSQL..."
docker-compose up -d postgres

# Attendre que PostgreSQL soit prêt
echo "⏳ Attente que PostgreSQL soit prêt..."
sleep 15

# Vérifier que PostgreSQL est prêt
until docker-compose exec -T postgres pg_isready -U postgres; do
    echo "⏳ Attente que PostgreSQL soit complètement prêt..."
    sleep 2
done

echo "🔄 Restauration de la base de données..."

# Supprimer la base existante si elle existe
docker-compose exec -T postgres psql -U postgres -c "DROP DATABASE IF EXISTS souqly;"

# Créer une nouvelle base de données
docker-compose exec -T postgres psql -U postgres -c "CREATE DATABASE souqly;"

# Restaurer le backup
docker-compose exec -T postgres psql -U postgres -d souqly < "$LATEST_BACKUP"

if [ $? -eq 0 ]; then
    echo "✅ Restauration terminée avec succès !"
    echo "📊 Base de données restaurée depuis : $LATEST_BACKUP"
else
    echo "❌ Erreur lors de la restauration"
    exit 1
fi

# Le seed est maintenant intégré dans le backup, pas besoin de l'exécuter séparément
echo "🌱 Le script de seed est intégré dans le backup et sera exécuté automatiquement"

echo "🚀 Démarrage de tous les services..."
docker-compose up -d

echo "✅ Restauration terminée !" 