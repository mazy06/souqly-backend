#!/bin/bash

# Script pour changer le mot de passe de l'utilisateur admin@souqly.com
# Usage: ./reset-admin-password.sh [nouveau_mot_de_passe]

NEW_PASSWORD=${1:-"admin123"}

echo "🔄 Changement du mot de passe pour admin@souqly.com..."
echo "Nouveau mot de passe: $NEW_PASSWORD"
echo ""

# Appel de l'endpoint
curl -X POST "http://localhost:8080/api/auth/admin/reset-password" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "newPassword=$NEW_PASSWORD" \
  -w "\n\nStatus: %{http_code}\n"

echo ""
echo "✅ Si le statut est 200, le mot de passe a été changé avec succès !"
echo "Tu peux maintenant te connecter avec:"
echo "Email: admin@souqly.com"
echo "Mot de passe: $NEW_PASSWORD" 