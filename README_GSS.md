# GSS — Gestion de salle de sport

Application JavaFX + JDBC + MySQL/MariaDB.

## Environnement

- Java 21
- JavaFX 21.0.6
- Maven
- MySQL Connector/J 9.4.0
- BCrypt (jBCrypt)
- OpenPDF
- IntelliJ IDEA / Maven

## 1. Base de données

Le script est dans :

`src/main/java/com/gss/gss/database/database.sql`

Il crée la base `gestion_salle_sport` et les tables dans l'ordre correct des dépendances.

### Connexion actuelle

Le projet utilise :

- Hôte : `localhost`
- Port : `3307`
- Base : `gestion_salle_sport`
- Utilisateur : `root`
- Mot de passe : vide

Ces paramètres sont dans :

`src/main/java/com/gss/gss/database/DatabaseConfig.java`

Si ton serveur MySQL/MariaDB utilise le port 3306, change uniquement `3307` en `3306`.

## 2. Premier utilisateur administrateur

Le mot de passe est vérifié avec BCrypt. Il ne faut donc pas mettre le mot de passe en clair dans la base.

Le plus simple est de créer un utilisateur depuis l'application lorsque la base contient déjà un administrateur utilisable, ou d'insérer un hash BCrypt généré par l'application.

## 3. Lancer l'application

Depuis IntelliJ :

- Ouvrir le projet Maven.
- Vérifier que le SDK est Java 21.
- Vérifier que MySQL/MariaDB est démarré.
- Vérifier le port dans `DatabaseConfig.java`.
- Lancer `com.gss.gss.Main`.

Avec Maven :

`mvn clean javafx:run`

ou, avec le wrapper :

`./mvnw clean javafx:run`

## 4. Dashboard

Le dashboard charge maintenant depuis la base :

- nombre total de membres ;
- nombre d'abonnements actifs et non expirés ;
- nombre total de paiements ;
- nombre total de séances ;
- fréquentation confirmée des 7 derniers jours ;
- utilisateur actuellement connecté ;
- date et heure mises à jour chaque seconde.

## 5. Reçu de paiement

Après un nouveau paiement validé, l'application génère un PDF `recu_<reference>.pdf` contenant notamment :

- référence ;
- nom du membre ;
- `membre_id` ;
- abonnement et période ;
- date/heure du paiement ;
- mode de paiement ;
- montant ;
- statut.

## 6. Architecture

Le projet conserve l'architecture :

`FXML -> Controller -> Service -> DAO -> JDBC -> MySQL/MariaDB`

Les données spécifiques au dashboard sont centralisées dans `DashboardService`.
