# Test de l'Endpoint mark-as-sold

## Objectif
Vérifier que l'endpoint backend `/api/products/{id}/mark-as-sold` fonctionne correctement et met à jour le statut du produit en base de données.

## Fonctionnalités Ajoutées

### 1. ProductController.java
- ✅ Ajouté l'endpoint `POST /api/products/{id}/mark-as-sold`
- ✅ Authentification requise (`@PreAuthorize("isAuthenticated()")`)
- ✅ Gestion d'erreur avec codes HTTP appropriés
- ✅ Validation que l'utilisateur est le vendeur du produit

### 2. ProductService.java
- ✅ Ajouté la méthode `markAsSold(Long productId, Long sellerId)`
- ✅ Transactionnel pour garantir la cohérence des données
- ✅ Vérification des autorisations (seul le vendeur peut marquer comme vendu)
- ✅ Mise à jour du statut en `SOLD` et désactivation du produit
- ✅ Logs de débogage pour tracer les opérations

## Test de l'Endpoint

### Prérequis
1. **Backend démarré** et accessible
2. **Base de données** connectée
3. **Utilisateur authentifié** avec des produits

### Étapes de Test

#### 1. Test avec Authentification
```bash
# Récupérer un token d'authentification
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password"
  }'

# Marquer un produit comme vendu
curl -X POST http://localhost:8080/api/products/1/mark-as-sold \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json"
```

#### 2. Test sans Authentification
```bash
# Doit retourner 401 Unauthorized
curl -X POST http://localhost:8080/api/products/1/mark-as-sold \
  -H "Content-Type: application/json"
```

#### 3. Test avec Mauvais Vendeur
```bash
# Doit retourner 400 Bad Request si l'utilisateur n'est pas le vendeur
curl -X POST http://localhost:8080/api/products/1/mark-as-sold \
  -H "Authorization: Bearer OTHER_USER_TOKEN" \
  -H "Content-Type: application/json"
```

### Réponses Attendues

#### Succès (200 OK)
```json
{
  "id": 1,
  "title": "Produit vendu",
  "description": "Description du produit",
  "price": 100.0,
  "status": "SOLD",
  "isActive": false,
  "seller": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe"
  },
  "category": {
    "id": 1,
    "label": "Électronique"
  }
}
```

#### Erreur 401 (Non authentifié)
```json
{
  "error": "Unauthorized",
  "message": "Access Denied"
}
```

#### Erreur 400 (Mauvais vendeur)
```json
{
  "error": "Bad Request",
  "message": "Vous n'êtes pas autorisé à modifier ce produit"
}
```

#### Erreur 404 (Produit non trouvé)
```json
{
  "error": "Not Found",
  "message": "Produit non trouvé"
}
```

## Vérification en Base de Données

### Requête SQL pour vérifier le statut
```sql
-- Vérifier le statut d'un produit
SELECT id, title, status, is_active, updated_at 
FROM products 
WHERE id = 1;

-- Vérifier tous les produits vendus
SELECT id, title, status, seller_id, updated_at 
FROM products 
WHERE status = 'SOLD';

-- Vérifier les produits d'un vendeur spécifique
SELECT id, title, status, is_active 
FROM products 
WHERE seller_id = 1;
```

### Résultats Attendus
- `status` doit être `SOLD`
- `is_active` doit être `false`
- `updated_at` doit être mis à jour avec la date/heure actuelle

## Test Intégré Frontend-Backend

### Étapes
1. **Ouvrir l'application** frontend
2. **Se connecter** avec un compte utilisateur
3. **Aller sur un produit** que vous avez créé
4. **Effectuer un paiement** (simulation)
5. **Vérifier les logs backend** pour voir l'appel à `markAsSold`
6. **Vérifier en base** que le statut a changé
7. **Retourner à l'accueil** et vérifier le badge "Vendu"

### Logs Backend Attendus
```
[ProductService] Produit 1 marqué comme vendu par l'utilisateur 1
[ProductController] markProductAsSold appelé pour le produit 1
```

## Dépannage

### Si l'endpoint retourne 401 :
- Vérifier que l'utilisateur est authentifié
- Vérifier que le token Bearer est valide
- Vérifier la configuration de sécurité

### Si l'endpoint retourne 400 :
- Vérifier que l'utilisateur est bien le vendeur du produit
- Vérifier que le produit existe
- Vérifier les logs pour plus de détails

### Si le statut ne change pas en base :
- Vérifier que la transaction est bien commitée
- Vérifier les logs de la base de données
- Vérifier que le service est bien transactionnel

### Si le frontend ne voit pas le changement :
- Vérifier que le cache est vidé
- Redémarrer l'application frontend
- Vérifier que les données sont bien rafraîchies

## Notes Techniques
- L'endpoint nécessite une authentification JWT
- Seul le vendeur du produit peut le marquer comme vendu
- Le produit est automatiquement désactivé (`isActive = false`)
- La méthode est transactionnelle pour garantir la cohérence
- Les logs permettent de tracer les opérations 