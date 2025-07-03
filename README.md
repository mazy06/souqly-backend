# Souqly Backend

Bienvenue sur le backend Spring Boot de Souqly, la plateforme de marketplace.

## Prérequis

- Java 17+
- Maven 3.8+
- PostgreSQL (ou autre SGBD compatible)
- (Optionnel) Docker

## Démarrage rapide

1. **Configurer la base de données**

   Créez une base PostgreSQL et renseignez les variables dans `src/main/resources/application.properties` :
   ```
   spring.datasource.url=jdbc:postgresql://localhost:5432/souqly
   spring.datasource.username=VOTRE_USER
   spring.datasource.password=VOTRE_MDP
   ```

2. **Lancer l’application**

   ```bash
   mvn spring-boot:run
   ```

   L’API sera disponible sur [http://localhost:8080](http://localhost:8080).

3. **Documentation API**

   Une fois l’app lancée, accédez à la documentation Swagger/OpenAPI :
   ```
   http://localhost:8080/swagger-ui.html
   ```

## Structure du projet

- `src/main/java/io/mazy/souqly_backend/`
  - `controller/` : Contrôleurs REST (endpoints)
  - `service/` : Logique métier
  - `repository/` : Accès base de données (JPA)
  - `model/` : Entités JPA
  - `dto/` : Objets de transfert de données
  - `config/` : Configuration (sécurité, CORS, etc.)

- `src/main/resources/`
  - `application.properties` : Configuration Spring Boot
  - `db/migration/` : Scripts de migration (Flyway)

## Authentification

- JWT (JSON Web Token)
- Endpoints d’inscription, connexion, profil utilisateur
- Voir la documentation [JWT_AUTHENTICATION.md](../souqly-frontend/documentation/JWT_AUTHENTICATION.md) côté frontend pour l’intégration

## Scripts utiles

- **Migration de base de données** (Flyway) : Les scripts SQL sont dans `src/main/resources/db/migration/`
- **Tests** :  
  ```bash
  mvn test
  ```

## Déploiement

- L’application peut être packagée en JAR :
  ```bash
  mvn clean package
  java -jar target/souqly-backend-*.jar
  ```
- Ou déployée via Docker (ajoutez un `Dockerfile` si besoin)

---

Pour toute question, ouvrez une issue sur le dépôt ou contactez l’équipe Souqly. 