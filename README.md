# TaskApp DevSecOps

Application Java (Spring Boot) de gestion de taches, livree par une chaine CI/CD securisee.

## Architecture

GitHub -> Jenkins -> Maven (build + tests) -> SonarQube -> Dependency-Check
-> Docker build -> Trivy -> Docker Hub -> Kubernetes (Minikube) -> OWASP ZAP

## Prerequis

- Ubuntu, Java 17, Maven 3.9
- Git, Docker, Minikube, kubectl
- Jenkins, SonarQube

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

## Branches

- `main` : version stable
- `develop` : integration
- `feature/*` : nouvelles fonctionnalites
