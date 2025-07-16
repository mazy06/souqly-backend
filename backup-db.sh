#!/bin/bash

echo "🗄️  Sauvegarde de la base de données PostgreSQL..."

# Créer le dossier de backup s'il n'existe pas
mkdir -p ./backups

# Nom du fichier de backup avec timestamp
BACKUP_FILE="./backups/souqly_backup_$(date +%Y%m%d_%H%M%S).sql"

# Vérifier si le conteneur PostgreSQL est en cours d'exécution
if docker-compose ps postgres | grep -q "Up"; then
    echo "📦 Conteneur PostgreSQL trouvé, création du backup..."
    
    # Créer le backup
    docker-compose exec -T postgres pg_dump -U postgres souqly > "$BACKUP_FILE"
    
    if [ $? -eq 0 ]; then
        echo "✅ Backup de base créé avec succès"
    else
        echo "❌ Erreur lors de la création du backup"
        exit 1
    fi
    
    # Ajouter le seed au backup
    echo "🌱 Ajout du script de seed au backup..."
    if [ -f "./seed-database.sql" ]; then
        echo "" >> "$BACKUP_FILE"
        echo "-- Script de seed pour alimenter la base de données" >> "$BACKUP_FILE"
        echo "-- Ajouté automatiquement lors du backup" >> "$BACKUP_FILE"
        echo "" >> "$BACKUP_FILE"
        cat ./seed-database.sql >> "$BACKUP_FILE"
        
        echo "✅ Script de seed ajouté au backup"
        echo "📊 Taille finale du fichier : $(du -h "$BACKUP_FILE" | cut -f1)"
    else
        echo "⚠️  Script de seed non trouvé : ./seed-database.sql"
        echo "📊 Taille du fichier : $(du -h "$BACKUP_FILE" | cut -f1)"
    fi
else
    echo "⚠️  Conteneur PostgreSQL n'est pas en cours d'exécution"
    echo "💡 Lancement des conteneurs pour créer un backup..."
    docker-compose up -d postgres
    
    # Attendre que PostgreSQL soit prêt
    echo "⏳ Attente que PostgreSQL soit prêt..."
    sleep 10
    
    # Créer le backup
    docker-compose exec -T postgres pg_dump -U postgres souqly > "$BACKUP_FILE"
    
    if [ $? -eq 0 ]; then
        echo "✅ Backup de base créé avec succès"
    else
        echo "❌ Erreur lors de la création du backup"
        exit 1
    fi
    
    # Ajouter le seed au backup
    echo "🌱 Ajout du script de seed au backup..."
    if [ -f "./seed-database.sql" ]; then
        echo "" >> "$BACKUP_FILE"
        echo "-- Script de seed pour alimenter la base de données" >> "$BACKUP_FILE"
        echo "-- Ajouté automatiquement lors du backup" >> "$BACKUP_FILE"
        echo "" >> "$BACKUP_FILE"
        cat ./seed-database.sql >> "$BACKUP_FILE"
        
        echo "✅ Script de seed ajouté au backup"
        echo "📊 Taille finale du fichier : $(du -h "$BACKUP_FILE" | cut -f1)"
    else
        echo "⚠️  Script de seed non trouvé : ./seed-database.sql"
        echo "📊 Taille du fichier : $(du -h "$BACKUP_FILE" | cut -f1)"
    fi
fi

echo "🔄 Arrêt des conteneurs..."
docker-compose down

echo "✅ Opération terminée !" 