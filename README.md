# Projet Spring Security Auth

## Description
Ce projet est une application Spring Boot qui met en œuvre plusieurs méthodes d’authentification et de gestion des utilisateurs :
* **Authentification par Basic Auth**
  
Utilisée principalement à des fins de test, cette méthode repose sur l’envoi des identifiants (username et password) encodés en Base64 dans l’en-tête HTTP. Elle permet de valider l’identité de l’utilisateur, mais n’intègre pas de mécanismes avancés d’autorisation. 

* **Authentification par formulaire (Form Login)**
  
Spring Security fournit un formulaire standard permettant à l’utilisateur de saisir ses identifiants. Après authentification réussie, une session est créée et associée à l’utilisateur. Cette approche illustre la distinction entre authentification (connexion via le formulaire) et autorisation (accès restreint aux ressources selon le rôle attribué).

* **Authentification stateless avec JWT (JSON Web Token)**
  
Dans ce mécanisme, une fois l’utilisateur authentifié, un token signé est généré et transmis au client. Ce token contient des informations sur l’identité et les rôles de l’utilisateur. À chaque requête, le client présente ce token, permettant au serveur de valider l’identité et de contrôler l’accès aux ressources protégées (autorisation basée sur les rôles).

* **Authentification par OAuth2 (Google)**
  
L’application délègue l’authentification à un fournisseur externe (Google). L’utilisateur est redirigé vers Google pour prouver son identité. Une fois authentifié, l’application obtient un jeton d’accès et peut récupérer des informations telles que le nom ou l’email. L’autorisation est ensuite appliquée au sein de l’application en fonction des rôles attribués. Ce mécanisme renforce la sécurité et améliore l’expérience utilisateur en supprimant la gestion locale des mots de passe.

## Stack technique
* Spring boot: 3.5.3
* Spring web
* Spring Data JPA
* Spring Security
* JWT
* OAuth Client
* Lombok

## Prérequis
* Java 17
* Postgre SQL
* Maven
* Compte Google Cloud configuré avec OAuth2 Client ID et Client Secret

## Installation et Lancement
1. Clôner le projet
```bash
git clone https://github.com/Math-Baba/Springboot_authentication_jwt.git
cd Springboot_authentication_jwt
```
2. Configurer la base PostgreSQL
   
Créez une base sur PostgreSQL (ex: security_db) puis mettre à jour **application.poperties** :
```bash
spring.datasource.url=jdbc:postgresql://localhost:5432/security_db
spring.datasource.username=postgres
spring.datasource.password=motdepasse # à modifier

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```
3. Configurer la clé JWT
   
Toujours dans **application.properties**, ajoutrez une clé secrète et un temps d'expiration de la clé
```bash
app.secret-key= # Insérez une clé JWT
app.expiration-time= # Temps d'expiration de la clé en millisecondes
```
4. Configurer OAuth2 Google
   
Toujours dans **application.properties**, ajoutez le client-id et le client-secret obtenu grâce à la création d'identifiants sur Google Cloud Console.
```bash
spring.security.oauth2.client.registration.google.client-id= # Insérez votre client-id
spring.security.oauth2.client.registration.google.client-secret= # Insérez votre client-secret
spring.security.oauth2.client.registration.google.scope=openid,profile,email
spring.security.oauth2.client.registration.google.redirect-uri=http://localhost:8080/login/oauth2/code/google
```
⚠️ Dans Google Cloud Console, mettre comme URI de redirection :
```bash
http://localhost:8080/login/oauth2/code/google
```

5. Lancez l'application
```bash
mvn spring-boot:run
```
## Auteur
**Math-Baba** - [GitHub](https://github.com/Math-Baba)
