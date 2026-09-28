# Image d'exécution légère (JRE 17 sur Alpine)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Le jar est produit par "mvn package" (étape Package du pipeline Jenkins)
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
