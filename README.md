# TaskApp DevSecOps

Application Java (Spring Boot) de gestion de taches, livree par une chaine CI/CD securisee.

## Architecture

GitHub -> Jenkins -> Maven (build + tests) -> SonarQube (SAST, Quality Gate)
-> OWASP Dependency-Check -> Docker build -> Trivy -> Docker Hub
-> Kubernetes (Minikube) -> OWASP ZAP (DAST)

## Prerequis

- Ubuntu, Java 17, Maven 3.9, Git
- Docker, Minikube, kubectl
- Jenkins (Java 21, port 8081), SonarQube (Docker, port 9000)
- Comptes : GitHub, Docker Hub, cle API NVD

## Lancer en local

    mvn clean package
    java -jar target/taskapp-0.0.1-SNAPSHOT.jar --server.port=9090

## API

| Methode | URL                  | Description             |
|---------|----------------------|-------------------------|
| GET     | /api/tasks           | Lister les taches       |
| POST    | /api/tasks           | Creer une tache         |
| PUT     | /api/tasks/{id}/done | Marquer une tache faite |
| DELETE  | /api/tasks/{id}      | Supprimer une tache     |
| GET     | /actuator/health     | Etat de l'application   |

## Docker

    docker build -t taskapp:1.0 .
    docker run -d -p 9090:8080 taskapp:1.0

Image de base officielle eclipse-temurin:17-jre-alpine, execution avec un utilisateur non root.

## Kubernetes

    minikube start
    kubectl apply -f k8s/
    curl $(minikube ip):30080/actuator/health

Deployment avec sondes liveness/readiness et limites de ressources, Service NodePort 30080.

## Pipeline Jenkins

Stages : Checkout, Build and Test, SonarQube Analysis, Dependency Check, Docker Build,
Trivy Scan, Docker Push, Deploy to Kubernetes, OWASP ZAP.

Declenchement : scrutation du depot GitHub toutes les 2 minutes.
Secrets : credentials Jenkins (dockerhub, sonar-token, nvd-api-key), aucun secret dans le depot.

## Politique de securite

- SonarQube : Quality Gate bloquant (nouveaux bugs, vulnerabilites, hotspots non revus, maintenabilite).
- Dependency-Check : echec si CVSS >= 9.
- Trivy : echec si vulnerabilite CRITICAL avec correctif ; les HIGH sont documentees.
- ZAP : scan baseline, rapport conserve et alertes analysees.

## Image publiee

Docker Hub : nourelislem123/taskapp (tags : numero de build et latest)

## Branches

- main : version stable
- develop : integration
- feature/* : fonctionnalites
