# Image de base Java légère (Alpine = ~50Mo vs ~300Mo pour Ubuntu)
FROM eclipse-temurin:17-jre-alpine

# Dossier de travail dans le conteneur
WORKDIR /app

# Copie le JAR compilé par Maven dans le conteneur
COPY target/*.jar monitor.jar

# Documente le port utilisé par Spring Boot (informatif uniquement)
EXPOSE 8080

# Commande de démarrage du conteneur
ENTRYPOINT ["java", "-jar", "monitor.jar"]